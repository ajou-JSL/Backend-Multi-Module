package study.moum.community.likes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.domain.repository.MemberRepository;
import study.moum.community.article.domain.article.ArticleEntity;
import study.moum.community.article.domain.article.ArticleRepository;
import study.moum.community.likes.domain.LikesEntity;
import study.moum.community.likes.domain.LikesRepository;
import study.moum.community.likes.dto.LikesDto;
import study.moum.global.error.ErrorCode;
import study.moum.global.error.exception.CustomException;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;

    /**
     좋아요 등록(생성)
     */
    @Transactional
    public LikesDto.Response createLikes(String memberName, int articleId) {

        ArticleEntity article = findArticle(articleId);
        MemberEntity member = findMember(memberName);

        LikesDto.Request likesRequest = LikesDto.Request.builder()
                .member(member)
                .article(article)
                .build();

        // 유저이름이 게시글작성자랑 똑같으면 에러
        if(memberName.equals(article.getAuthor().getUsername())){
            throw new CustomException(ErrorCode.CANNOT_CREATE_SELF_LIKES);
        }

        LikesEntity newLikes = likesRequest.toEntity();
        likesRepository.save(newLikes);

        // 좋아요 +1 후 저장
        article.updateLikesCount(1);
        articleRepository.save(article);

        return new LikesDto.Response(newLikes);
    }

    /**
     좋아요 삭제
     */
    @Transactional
    public LikesDto.Response deleteLikes(int articleId, String memberName) {

        MemberEntity member = findMember(memberName);

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

        return new LikesDto.Response(likesEntity);
    }

    public ArticleEntity findArticle(int articleId){
        return articleRepository.findById(articleId)
                .orElseThrow(()-> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    public MemberEntity findMember(String memberName){

        if(memberRepository.findByUsername(memberName) == null){
            throw new CustomException(ErrorCode.MEMBER_NOT_EXIST);
        }
        return memberRepository.findByUsername(memberName);
    }

}
