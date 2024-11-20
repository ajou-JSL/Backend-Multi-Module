package jsl.moum.community.perform.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.article.dto.ArticleDto;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.community.perform.domain.entity.PerformMember;
import jsl.moum.community.perform.domain.repository.PerformArticleRepository;
import jsl.moum.community.perform.domain.repository.PerformArticleRepositoryCustom;
import jsl.moum.community.perform.dto.PerformArticleDto;
import jsl.moum.community.perform.dto.PerformArticleUpdateDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.lifecycle.domain.LifecycleEntity;
import jsl.moum.moum.lifecycle.domain.LifecycleRepository;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberRepositoryCustom;
import jsl.moum.moum.team.domain.TeamRepository;
import jsl.moum.objectstorage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
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
                .genres(requestDto.getGenres())
                .team(team)
                .moum(moum)
                .build();

        // performMembers.forEach(pm -> pm.assignPerformanceArticle(newPerformArticleEntity));
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
    @Transactional(readOnly = true)
    public PerformArticleDto.Response getPerformArticleById(int performArticleId){
        PerformArticleEntity target = performArticleRepository.findById(performArticleId)
                .orElseThrow(()-> new CustomException(ErrorCode.ILLEGAL_ARGUMENT));

        return new PerformArticleDto.Response(target);
    }

    /*
        리스트 조회
    */
    @Transactional(readOnly = true)
    public List<PerformArticleDto.Response> getAllPerformArticle(int page, int size){
        List<PerformArticleEntity> performArticles = performArticleRepository.findAll(PageRequest.of(page, size)).getContent();

        List<PerformArticleDto.Response> responseList = performArticles.stream()
                .map(PerformArticleDto.Response::new)
                .toList();

        return responseList;
    }

    /*
        이달의 공연 게시글 리스트 조회
    */
    @Transactional(readOnly = true)
    public List<PerformArticleDto.Response> getAllThisMonthPerformArticles(int page, int size){
        List<PerformArticleEntity> performArticles = performArticleRepositoryCustom.getThisMonthPerformArticles(page, size);

        List<PerformArticleDto.Response> responseList = performArticles.stream()
                .map(PerformArticleDto.Response::new)
                .toList();

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
        return storageService.uploadFile(key, file);
    }

}
