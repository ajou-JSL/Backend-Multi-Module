package jsl.moum.community.comment.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.community.article.dto.ArticleDetailsDto;
import jsl.moum.community.comment.domain.CommentEntity;
import jsl.moum.community.comment.domain.CommentRepository;
import jsl.moum.community.comment.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.article.domain.article_details.ArticleDetailsEntity;
import jsl.moum.community.article.domain.article_details.ArticleDetailsRepository;
import jsl.moum.community.article.domain.article.ArticleRepository;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.global.error.exception.MemberNotExistException;
import jsl.moum.global.error.exception.NoAuthorityException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final ArticleDetailsRepository articleDetailsRepository;
    private final MemberRepository memberRepository;

    /**
     * 댓글 생성
     */
    @Transactional
    public CommentDto.Response createComment(CommentDto.Request commentRequestDto, String username, int articleId){

        MemberEntity author = memberRepository.findByUsername(username);
        if(author == null){
            throw new MemberNotExistException();
        }
        ArticleEntity article = getArticle(articleId);
        ArticleDetailsEntity articleDetails = getArticleDetails(articleId);

        CommentDto.Request commentRequest = CommentDto.Request.builder()
                .articleDetails(articleDetails)
                .author(author)
                .content(commentRequestDto.getContent())
                .build();


        // 댓글dto -> entity 변환 후 댓글 저장
        CommentEntity newComment = commentRequest.toEntity();
        commentRepository.save(newComment);

        // 게시글에 조회수 +1 하고 저장
        article.commentsCountUp();
        articleRepository.save(article);

        // 게시글_상세 테이블에 댓글 추가됐으니 게시글_상세 저장
        articleDetailsRepository.save(articleDetails);

        author.updateMemberExpAndRank(1);

        return new CommentDto.Response(newComment);
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommentDto.Response updateComment(CommentDto.Request commentRequestDto, String username, int commentId){

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(()->new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 작성자-로그인유저 일치 여부 확인
        checkAuthor(username, comment.getAuthor().getUsername());

        // 새로 요청된 content 적용 후 저장
        String newContent = commentRequestDto.getContent();
        comment.updateComment(newContent);
        commentRepository.save(comment);

        return new CommentDto.Response(comment);
    }


    /**
     * 댓글 삭제
     */
    @Transactional
    public CommentDto.Response deleteComment(String username, int commentId){

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(()->new CustomException(ErrorCode.COMMENT_ALREADY_DELETED));

        int articleId = comment.getArticleDetails().getArticleId();
        ArticleEntity article = getArticle(articleId);
        article.commentsCountDown();

        // 작성자-로그인유저 일치 여부 확인
        checkAuthor(username, comment.getAuthor().getUsername());

        commentRepository.deleteById(commentId);

        comment.getAuthor().updateMemberExpAndRank(-1);

        return new CommentDto.Response(comment);
    }

    private ArticleDetailsEntity getArticleDetails(int articleDetailsId) {
        return articleDetailsRepository.findById(articleDetailsId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    private ArticleEntity getArticle(int articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    private void checkAuthor(String memberName, String author) {
        if (!memberName.equals(author)) {
            throw new NoAuthorityException();
        }
    }

    /**
     * 댓글 목록 조회
     */

    public List<CommentDto.Response> getCommentsByArticleId(int articleId){
        ArticleDetailsEntity articleDetails = getArticleDetails(articleId);
        List<CommentEntity> comments = articleDetails.getComments();
        return comments.stream()
                .map(CommentDto.Response::new)
                .toList();
    }
}
