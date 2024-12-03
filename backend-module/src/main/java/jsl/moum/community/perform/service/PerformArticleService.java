package jsl.moum.community.perform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.community.perform.domain.entity.PerformMember;
import jsl.moum.community.perform.domain.repository.PerformArticleRepository;
import jsl.moum.community.perform.domain.repository.PerformArticleRepositoryCustom;
import jsl.moum.community.perform.dto.PerformArticleDto;
import jsl.moum.community.perform.dto.PerformArticleUpdateDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import jsl.moum.moum.lifecycle.domain.repository.LifecycleRepository;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberRepositoryCustom;
import jsl.moum.moum.team.domain.TeamRepository;
import jsl.moum.objectstorage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformArticleService {

    private final PerformArticleRepository performArticleRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final StorageService storageService;
    private final TeamMemberRepositoryCustom teamMemberRepositoryCustom;
    private final PerformArticleRepositoryCustom performArticleRepositoryCustom;
    private final LifecycleRepository lifecycleRepository;
    private final ObjectMapper objectMapper;

    @Value("${ncp.object-storage.bucket}")
    private String bucket;


    /*
        생성
     */
    @Transactional
    public PerformArticleDto.Response createPerformArticle(String username, PerformArticleDto.Request requestDto, MultipartFile file) throws IOException {
        MemberEntity member = findMember(username);
        TeamEntity team = findTeam(requestDto.getTeamId());
        LifecycleEntity moum = findMoum(requestDto.getMoumId());

        // 이미 만든 공연게시글 있으면 안됨
        if(performArticleRepositoryCustom.isAlreadyExistPerformArticleByMoumId(moum.getId())){
            throw new CustomException(ErrorCode.PERFORM_ARTICLE_ALREADY_EXIST);
        }

        if (!isLeader(team, member)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        String imageUrl = uploadImage(requestDto.getPerformanceName(), file);

        List<MemberEntity> allMembers = teamMemberRepositoryCustom.findAllMembersByTeamId(team.getId());
        List<Integer> requestedMemberIds = requestDto.getMembersId();

        List<PerformMember> performMembers = allMembers.stream()
                .filter(m -> requestedMemberIds.contains(m.getId()) || m.getId() == member.getId())
                .map(m -> PerformMember.builder()
                        .performanceArticle(null)
                        .member(m)
                        .build()
                )
                .collect(Collectors.toList());

        PerformArticleEntity newPerformArticleEntity = PerformArticleEntity.builder()
                .performanceName(requestDto.getPerformanceName())
                .performanceDescription(requestDto.getPerformanceDescription())
                .performanceLocation(requestDto.getPerformanceLocation())
                .performanceStartDate(requestDto.getPerformanceStartDate())
                .performanceEndDate(requestDto.getPerformanceEndDate())
                .performancePrice(requestDto.getPerformancePrice())
                .performanceImageUrl(imageUrl)
                .performMembers(performMembers)
                .genre(requestDto.getGenre())
                .team(team)
                .moum(moum)
                .viewCount(0)
                .likesCount(0)
                .build();

        for (PerformMember performMember : performMembers) {
            performMember.assignPerformanceArticle(newPerformArticleEntity);
        }

        performArticleRepository.save(newPerformArticleEntity);
        return new PerformArticleDto.Response(newPerformArticleEntity);
    }

    /*
        수정
     */
    @Transactional
    public PerformArticleUpdateDto.Response updatePerformArticle(String username,int performArticleId ,PerformArticleUpdateDto.Request requestDto, MultipartFile file) throws IOException {
        MemberEntity member = findMember(username);
        PerformArticleEntity performArticleEntity = findPerformArticle(performArticleId);
        TeamEntity team = findTeam(performArticleEntity.getTeam().getId());
        LifecycleEntity moum = findMoum(performArticleEntity.getMoum().getId());

        if (!isLeader(team, member)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }


        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = uploadImage(requestDto.getPerformanceName(), file);
        }

        List<Integer> requestedMemberIds = requestDto.getMembersId();
        if (requestedMemberIds != null) {
            List<MemberEntity> allMembers = teamMemberRepositoryCustom.findAllMembersByTeamId(team.getId());

            List<PerformMember> updatedPerformMembers = allMembers.stream()
                    .filter(m -> requestedMemberIds.contains(m.getId()))
                    .map(m -> PerformMember.builder()
                            .performanceArticle(performArticleEntity)
                            .member(m)
                            .build())
                    .toList();

            performArticleEntity.getPerformMembers().clear();
            performArticleEntity.getPerformMembers().addAll(updatedPerformMembers);
        }

        // 공연 수정된 엔티티 저장
        performArticleEntity.updatePerformArticle(requestDto);
        performArticleRepository.save(performArticleEntity);

        return new PerformArticleUpdateDto.Response(performArticleEntity);

    }

    /*
        단건 조회
     */
    @Transactional
    public PerformArticleDto.Response getPerformArticleById(int performArticleId){
        PerformArticleEntity target = performArticleRepository.findById(performArticleId)
                .orElseThrow(()-> new CustomException(ErrorCode.ILLEGAL_ARGUMENT));

        target.updateViewCount(1);
        return new PerformArticleDto.Response(target);
    }

    /*
        모음의 공연게시글 단건 조회
    */
    @Transactional
    public PerformArticleDto.Response getPerformArticleByMoumId(int moumId){
        PerformArticleEntity target = performArticleRepositoryCustom.findPerformArticleByMoumId(moumId);
        if(target == null){
            throw new CustomException(ErrorCode.ILLEGAL_ARGUMENT);
        }

        return new PerformArticleDto.Response(target);
    }

    /*
        리스트 조회
    */
    @Transactional(readOnly = true)
    public List<PerformArticleDto.Response> getAllPerformArticle(int page, int size){
        List<PerformArticleEntity> performArticles = performArticleRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        List<PerformArticleDto.Response> responseList = performArticles.stream()
                .map(PerformArticleDto.Response::new)
                .toList();

        return responseList;
    }

    /*
        이달의 공연 게시글 리스트 조회
    */
    @Transactional(readOnly = true)
    public Page<PerformArticleDto.Response> getAllThisMonthPerformArticles(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<PerformArticleEntity> performArticles = performArticleRepositoryCustom.getThisMonthPerformArticles(pageable);

        Page<PerformArticleDto.Response> responseList = performArticles
                .map(PerformArticleDto.Response::new);

        return responseList;
    }

    /*
     공연 게시글 삭제
    */
    @Transactional
    public PerformArticleDto.Response deletePerformArticle(String username, int performArticleId) {
        MemberEntity member = findMember(username);
        PerformArticleEntity performArticleEntity = findPerformArticle(performArticleId);
        TeamEntity team = findTeam(performArticleEntity.getTeam().getId());

        if(!isLeader(team,member)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        String fileUrl = performArticleEntity.getPerformanceImageUrl();
        if (fileUrl != null || !fileUrl.isEmpty()) {
            String fileName = fileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            storageService.deleteFile(fileName);
        }

        performArticleRepository.deleteById(performArticleEntity.getId());
        return new PerformArticleDto.Response(performArticleEntity);
    }

    /*
        필터링으로 공연 게시글 조회
     */
    @Transactional
    public Page<PerformArticleDto.Response> getPerformArticleWithFiltering(PerformArticleDto.SearchDto searchDto, int page, int size) {

//        PerformArticleDto.SearchDto searchDto = null;
//        log.info("encodedString : {}", encodedString);
//        if (encodedString != null) {
//            try {
//                log.info("try 진입");
//                String decodedString = new String(Base64.getDecoder().decode(encodedString));
//                log.info("decodedString : {}", decodedString);
//                searchDto = objectMapper.readValue(decodedString, PerformArticleDto.SearchDto.class);
//                log.info("searchDto : {}", searchDto);
//            } catch (IllegalArgumentException | JsonProcessingException e) {
//                log.error(e.getMessage());
//                throw new CustomException(ErrorCode.BASE64_PROCESS_FAIL);
//            }
//        }
        Pageable pageable = PageRequest.of(page, size);
        Page<PerformArticleEntity> teams = performArticleRepositoryCustom.searchPerformArticlesWithFiltering(searchDto, pageable);

        Page<PerformArticleDto.Response> performArticleList = teams
                .map(PerformArticleDto.Response::new);

        return performArticleList;
    }


    public MemberEntity findMember(String username){
        MemberEntity member = memberRepository.findByUsername(username);
        if(member == null){
            throw new CustomException(ErrorCode.MEMBER_NOT_EXIST);
        }
        return member;
    }

    public TeamEntity findTeam(int teamId){
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(()-> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        return team;
    }

    public LifecycleEntity findMoum(int moumId){
        LifecycleEntity moum = lifecycleRepository.findById(moumId)
                .orElseThrow(()-> new CustomException(ErrorCode.MOUM_NOT_FOUND));

        return moum;
    }

    public PerformArticleEntity findPerformArticle(int id){
        return performArticleRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.ILLEGAL_ARGUMENT));
    }

    public Boolean isLeader(TeamEntity team, MemberEntity loginUser){
        if (team.getLeaderId() != loginUser.getId()) {
            return false;
        }

        return true;
    }

    public String uploadImage(String performanceName, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String key = "performs/" + performanceName+ "/" + originalFilename;
        return storageService.uploadImage(key, file);
    }

}
