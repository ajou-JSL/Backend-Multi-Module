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
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberRepositoryCustom;
import jsl.moum.moum.team.domain.TeamRepository;
import jsl.moum.objectstorage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformArticleService {

    private final PerformArticleRepository performArticleRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final StorageService storageService;
    private final TeamMemberRepositoryCustom teamMemberRepositoryCustom;
    private final PerformArticleRepositoryCustom performArticleRepositoryCustom;

    /*
        생성
     */
    @Transactional
    public PerformArticleDto.Response createPerformArticle(String username, PerformArticleDto.Request requestDto, MultipartFile file) throws IOException {
        MemberEntity member = findMember(username);
        TeamEntity team = findTeam(requestDto.getTeamId());

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
                .build();

        // performMembers.forEach(pm -> pm.assignPerformanceArticle(newPerformArticleEntity));
        for (PerformMember performMember : performMembers) {
            performMember.assignPerformanceArticle(newPerformArticleEntity);
        }

        performArticleRepository.save(newPerformArticleEntity);
        return new PerformArticleDto.Response(newPerformArticleEntity);
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
