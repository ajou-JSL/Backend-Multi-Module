package jsl.moum.community.likes.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.article.domain.article.ArticleRepository;
import jsl.moum.community.likes.domain.LikesEntity;
import jsl.moum.community.likes.domain.LikesRepository;
import jsl.moum.community.likes.domain.LikesRepositoryCustom;
import jsl.moum.community.likes.dto.LikesDto;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.community.perform.domain.repository.PerformArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final PerformArticleRepository performArticleRepository;
    private final LikesRepositoryCustom likesRepositoryCustom;

    public LikesDto.Toggle isMemberLikesArticle(int memberId, int articleId){
        boolean isLikedArticle = likesRepositoryCustom.isAlreadyLikesOnArticle(memberId, articleId);
        ArticleEntity article = findArticle(articleId);
        int likesCount = article.getLikesCount();
        return new LikesDto.Toggle(isLikedArticle, likesCount);
    }

    public LikesDto.Toggle isMemberLikesPerformArticle(int memberId, int performArticleId){
        boolean isLikedArticle = likesRepositoryCustom.isAlreadyLikesOnPerformArticle(memberId, performArticleId);
        PerformArticleEntity performArticle = findPerformArticle(performArticleId);
        int likesCount = performArticle.getLikesCount();
        return new LikesDto.Toggle(isLikedArticle, likesCount);
    }

    public LikesDto.Toggle toggleArticleLikes(int memberId, int articleId){
        LikesEntity likesEntity = likesRepository.findByArticleIdAndMemberId(articleId, memberId)
                .orElse(null);

        if(likesEntity == null){
            return createLikesByMemberId(memberId, articleId);

        } else{
            ArticleEntity article = findArticle(articleId);

            // 좋아요 삭제
            likesRepository.deleteById(likesEntity.getId());

            // 게시글 좋아요 수 감소 및 저장
            article.updateLikesCount(-1);
            articleRepository.save(article);
            article.getAuthor().updateMemberExpAndRank(-1);
            return new LikesDto.Toggle(false, article.getLikesCount());
        }
    }

    public LikesDto.Toggle togglePerformArticleLikes(int memberId, int performArticleId){
        LikesEntity likesEntity = likesRepository.findByPerformArticleIdAndMemberId(performArticleId, memberId)
                .orElse(null);
        if(likesEntity == null){
            return createPerformLikesByMemberId(memberId, performArticleId);
        } else{
            PerformArticleEntity performArticle = findPerformArticle(performArticleId);

            // 좋아요 삭제
            likesRepository.deleteById(likesEntity.getId());

            // 게시글 좋아요 수 감소 및 저장
            performArticle.updateLikesCount(-1);
            performArticleRepository.save(performArticle);
            performArticle.getTeam().updateTeamExpAndRank(-1);
            return new LikesDto.Toggle(false, performArticle.getLikesCount());
        }
    }

    private LikesDto.Toggle createLikesByMemberId(int memberId, int articleId){
        MemberEntity member = findMemberByMemberId(memberId);
        ArticleEntity article = findArticle(articleId);

        // 유저이름이 게시글작성자랑 똑같으면 에러
        if(memberId == article.getAuthor().getId()){
            throw new CustomException(ErrorCode.CANNOT_CREATE_SELF_LIKES);
        }

        LikesDto.Request likesRequest = LikesDto.Request.builder()
                .member(member)
                .article(article)
                .build();

        LikesEntity newLikes = likesRequest.toEntity();
        likesRepository.save(newLikes);

        // 좋아요 +1 후 저장
        article.updateLikesCount(1);
        articleRepository.save(article);
        article.getAuthor().updateMemberExpAndRank(1);
        return new LikesDto.Toggle(true, article.getLikesCount());
    }

    private LikesDto.Toggle createPerformLikesByMemberId(int memberId, int performArticleId){
        MemberEntity member = findMemberByMemberId(memberId);
        PerformArticleEntity performArticle = findPerformArticle(performArticleId);

        LikesDto.Request likesRequest = LikesDto.Request.builder()
                .member(member)
                .performArticle(performArticle)
                .build();

        LikesEntity newLikes = likesRequest.toEntity();
        likesRepository.save(newLikes);

        // 좋아요 +1 후 저장
        performArticle.updateLikesCount(1);
        performArticleRepository.save(performArticle);

        performArticle.getTeam().updateTeamExpAndRank(1);

        return new LikesDto.Toggle(true, performArticle.getLikesCount());
    }


    /**
     일반 게시글 좋아요 등록(생성)
     */
    @Transactional
    public LikesDto.Response createLikes(String memberName, int articleId) {

        ArticleEntity article = findArticle(articleId);
        MemberEntity member = findMemberByUsername(memberName);

        LikesDto.Request likesRequest = LikesDto.Request.builder()
                .member(member)
                .article(article)
                .build();

        // 유저이름이 게시글작성자랑 똑같으면 에러
        if(memberName.equals(article.getAuthor().getUsername())){
            throw new CustomException(ErrorCode.CANNOT_CREATE_SELF_LIKES);
        }

        // 이미 눌렀으면 에러
        if(likesRepositoryCustom.isAlreadyLikesOnArticle(member.getId(), article.getId())){
            throw new CustomException(ErrorCode.DUPLICATE_LIKES);
        }

        LikesEntity newLikes = likesRequest.toEntity();
        likesRepository.save(newLikes);

        // 좋아요 +1 후 저장
        article.updateLikesCount(1);
        articleRepository.save(article);

        article.getAuthor().updateMemberExpAndRank(1);

        return new LikesDto.Response(newLikes);
    }

    /**
     일반 게시글 좋아요 삭제
     */
    @Transactional
    public LikesDto.Response deleteLikes(String memberName, int articleId) {

        MemberEntity member = findMemberByUsername(memberName);

        // 해당 게시글에 대해 이 멤버가 누른 좋아요 찾기
        LikesEntity likesEntity = likesRepository.findByArticleIdAndMemberId(articleId, member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.LIKES_NOT_FOUND));

        // 유저 이름이랑 좋아요를 누른 사람의 이름이 다르면 에러
        if (!memberName.equals(likesEntity.getMember().getUsername())) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_OTHERS_LIKES);
        }

        // 좋아요 삭제
        likesRepository.deleteLikeByArticleIdAndMemberId(articleId, member.getId());

        // 게시글 좋아요 수 감소 및 저장
        ArticleEntity article = findArticle(articleId);
        article.updateLikesCount(-1);
        articleRepository.save(article);

        article.getAuthor().updateMemberExpAndRank(-1);

        return new LikesDto.Response(likesEntity);
    }


    /**
     공연 게시글 좋아요 등록(생성)
     */
    @Transactional
    public LikesDto.Response createPerformLikes(String memberName, int performArticleId) {

        PerformArticleEntity performArticle = findPerformArticle(performArticleId);
        MemberEntity member = findMemberByUsername(memberName);

        LikesDto.Request likesRequest = LikesDto.Request.builder()
                .member(member)
                .performArticle(performArticle)
                .build();

        // 이미 눌렀으면 에러
        if(likesRepositoryCustom.isAlreadyLikesOnPerformArticle(member.getId(), performArticle.getId())){
            throw new CustomException(ErrorCode.DUPLICATE_LIKES);
        }

        LikesEntity newLikes = likesRequest.toEntity();
        likesRepository.save(newLikes);

        // 좋아요 +1 후 저장
        //performArticle.updateLikesCount(1);
        performArticleRepository.save(performArticle);

        performArticle.getTeam().updateTeamExpAndRank(1);

        return new LikesDto.Response(newLikes);
    }

    /**
     공연 게시글 좋아요 삭제
     */
    @Transactional
    public LikesDto.Response deletePerformLikes(String memberName, int performArticleId) {

        MemberEntity member = findMemberByUsername(memberName);

        // 해당 게시글에 대해 이 멤버가 누른 좋아요 찾기
        LikesEntity likesEntity = likesRepository.findByPerformArticleIdAndMemberId(performArticleId, member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.LIKES_NOT_FOUND));

        // 유저 이름이랑 좋아요를 누른 사람의 이름이 다르면 에러
        if (!memberName.equals(likesEntity.getMember().getUsername())) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_OTHERS_LIKES);
        }

        // 좋아요 삭제
        likesRepository.deleteLikeByPerformArticleIdAndMemberId(performArticleId, member.getId());

        // 게시글 좋아요 수 감소 및 저장
        PerformArticleEntity performArticle = findPerformArticle(performArticleId);
        performArticle.updateLikesCount(-1);
        performArticleRepository.save(performArticle);

        performArticle.getTeam().updateTeamExpAndRank(-1);

        return new LikesDto.Response(likesEntity);
    }

    public ArticleEntity findArticle(int articleId){
        return articleRepository.findById(articleId)
                .orElseThrow(()-> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    public PerformArticleEntity findPerformArticle(int performArticleId){
        return performArticleRepository.findById(performArticleId)
                .orElseThrow(()-> new CustomException(ErrorCode.PERFORM_ARTICLE_NOT_FOUND));
    }

    public MemberEntity findMemberByUsername(String memberName){

        if(memberRepository.findByUsername(memberName) == null){
            throw new CustomException(ErrorCode.MEMBER_NOT_EXIST);
        }
        return memberRepository.findByUsername(memberName);
    }

    public MemberEntity findMemberByMemberId(Integer memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_EXIST));
    }

}
