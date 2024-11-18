package jsl.moum.community.article.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.community.article.domain.article.ArticleRepositoryCustom;
import jsl.moum.community.article.domain.article_details.ArticleDetailsEntity;
import jsl.moum.community.article.domain.article_details.ArticleDetailsRepositoryCustom;
import jsl.moum.community.article.dto.ArticleDetailsDto;
import jsl.moum.community.article.dto.ArticleDto;
import jsl.moum.objectstorage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.article.domain.article.ArticleRepository;
import jsl.moum.community.article.domain.article_details.ArticleDetailsRepository;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.global.error.exception.NeedLoginException;
import jsl.moum.global.error.exception.NoAuthorityException;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleDetailsRepository articleDetailsRepository;
    private final MemberRepository memberRepository;
    private final ArticleDetailsRepositoryCustom articleDetailsRepositoryCustom;
    private final StorageService storageService;
    private final ArticleRepositoryCustom articleRepositoryCustom;

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    /**
     * 게시글 작성
     */
    @Transactional
    public ArticleDto.Response postArticle(ArticleDto.Request articleRequestDto, MultipartFile file, String memberName) throws IOException {
        MemberEntity author = memberRepository.findByUsername(memberName);

        // article 테이블 -> title 작성
        ArticleDto.Request articleRequest = ArticleDto.Request.builder()
                .author(author)
                .title(articleRequestDto.getTitle())
                .category(articleRequestDto.getCategory())
                .build();

        // article 테이블 저장
        ArticleEntity newArticle = articleRequest.toEntity();
        articleRepository.save(newArticle);

        String fileUrl = "";
        if(file != null || !file.isEmpty()){
            fileUrl = uploadFile(newArticle.getId(), file);
        }

        // article_details 테이블 -> content 작성
        ArticleDetailsDto.Request articleDetailsRequestDto = ArticleDetailsDto.Request.builder()
                .content(articleRequestDto.getContent())
                .fileUrl(fileUrl) // 파일 URL 설정
                .build();

        // article_details 테이블 저장
        ArticleDetailsEntity newArticleDetails = articleDetailsRequestDto.toEntity();
        articleDetailsRepository.save(newArticleDetails);

        author.updateMemberExpAndRank(1);

        return new ArticleDto.Response(newArticle);

    }

    /**
     * 게시글 조회
     */
    @Transactional
    public ArticleDetailsDto.Response getArticleById(int articleDetailsId){
        ArticleDetailsEntity articleDetails = getArticleDetails(articleDetailsId);
        ArticleEntity article = getArticle(articleDetailsId);

        article.viewCountUp(); // 조회수 증가

        return new ArticleDetailsDto.Response(articleDetails, article);
    }

    /**
     * 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ArticleDto.Response> getArticleList(int page, int size) {

        List<ArticleEntity> articles = articleRepository.findAll(PageRequest.of(page, size)).getContent();

        List<ArticleDto.Response> articleResponseList = articles.stream()
                .map(ArticleDto.Response::new)
                .collect(Collectors.toList());

        return articleResponseList;
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public ArticleDetailsDto.Response updateArticleDetails(int articleDetailsId,
                                                           ArticleDetailsDto.Request articleDetailsRequestDto,
                                                           MultipartFile file,
                                                           String memberName) throws IOException {

        ArticleDetailsEntity articleDetails = getArticleDetails(articleDetailsId);
        ArticleEntity article = getArticle(articleDetailsId);
        String articleAuthor = article.getAuthor().getUsername();

        // 로그인유저 == 작성자 여부 체크
        checkAuthor(memberName, articleAuthor);

        String newTitle = articleDetailsRequestDto.getTitle();
        String newContent = articleDetailsRequestDto.getContent();
        ArticleEntity.ArticleCategories newCategory = articleDetailsRequestDto.getCategory();

        String existingFileUrl = articleDetails.getFileUrl();

        // 새로운 파일이 있을 경우 처리
        if (file != null || !file.isEmpty()) {
            if (existingFileUrl != null && !existingFileUrl.isEmpty()) {
                String existingFileName = existingFileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
                storageService.deleteFile(existingFileName); // S3에서 기존 파일 삭제
            }

            String newUrl = uploadFile(articleDetailsId, file);
//            String newFileName = "articles/" + articleDetailsId + "/" + file.getOriginalFilename(); // 폴더 구조에 맞게 설정
//            String newFileUrl = storageService.uploadFile(newFileName, file); // S3에 파일 업로드
            articleDetails.updateArticleImage(newUrl);
        }

        // article_details, article 둘 다 update
        articleDetails.updateArticleDetails(newContent);
        article.updateArticle(newTitle,newCategory);

        // article_details, article 둘 다 저장
        articleDetailsRepository.save(articleDetails);
        articleRepository.save(article);

        return new ArticleDetailsDto.Response(articleDetails, article);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public ArticleDto.Response deleteArticleDetails(int articleDetailsId, String memberName){

        ArticleDetailsEntity articleDetails = getArticleDetails(articleDetailsId);
        ArticleEntity article = getArticle(articleDetailsId);

        // 로그인유저 == 작성자 여부 체크
        String articleAuthor = article.getAuthor().getUsername();
        checkAuthor(memberName, articleAuthor);

        String fileUrl = articleDetails.getFileUrl();
        if (fileUrl != null || !fileUrl.isEmpty()) {
            // S3에서의 파일 경로 제거해서 key 추출하기 -> 경로 : /articles/{id}/{fileName} 이런식임
            String fileName = fileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8); // URL 디코딩
            storageService.deleteFile(fileName); // S3에서 파일 삭제
        }
        // article_details, article 테이블 둘 다 삭제
        articleDetailsRepository.deleteById(articleDetailsId);
        articleRepository.deleteById(articleDetailsId);

        article.getAuthor().updateMemberExpAndRank(-1);

        return new ArticleDto.Response(article);
    }

    /**
     * 실시간 인기 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ArticleDto.Response> getHotArticleList(int page, int size) {
        List<ArticleEntity> articles = articleRepositoryCustom.getAllHotArticles(page, size);

        return articles.stream()
                .map(ArticleDto.Response::new)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리에 따른 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ArticleDto.Response> getArticlesByCategory(ArticleEntity.ArticleCategories category, int page, int size) {
        List<ArticleEntity> articles;

        if (category == ArticleEntity.ArticleCategories.FREE_TALKING_BOARD) {
            articles = articleDetailsRepositoryCustom.findFreeTalkingArticles(page, size);
        } else if (category == ArticleEntity.ArticleCategories.RECRUIT_BOARD) {
            articles = articleDetailsRepositoryCustom.findRecruitingdArticles(page, size);
        } else {
            throw new CustomException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        return articles.stream()
                .map(ArticleDto.Response::new)
                .collect(Collectors.toList());
    }

    /**
     * 주어진 키워드를 사용하여 게시글을 검색
     */
    @Transactional(readOnly = true)
    public List<ArticleDto.Response> getArticleWithTitleSearch(String keyword, String category,int page, int size) {
        List<ArticleEntity> articles = articleDetailsRepositoryCustom.searchArticlesByTitleKeyword(keyword, category, page, size);

        List<ArticleDto.Response> articleResponseList = articles.stream()
                .map(ArticleDto.Response::new)
                .collect(Collectors.toList());

        return articleResponseList;
    }

    /**
     * 나의 즐겨찾기 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<ArticleDto.Response> getMyWishlist(String memberName,int page, int size) {
//        List<ArticleEntity> articles = articleRepositoryCustom.searchArticlesByTitleKeyword(keyword, category, page, size);
        int memberId = memberRepository.findByUsername(memberName).getId();

        List<ArticleEntity> articles = articleRepository.findLikedArticles(memberId);

        List<ArticleDto.Response> articleResponseList = articles.stream()
                .map(ArticleDto.Response::new)
                .collect(Collectors.toList());

        return articleResponseList;
    }


    private ArticleDetailsEntity getArticleDetails(int articleDetailsId) {
        return articleDetailsRepository.findById(articleDetailsId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    private ArticleEntity getArticle(int articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    private void checkAuthor(String memberName, String articleAuthor) {
        if (!memberName.equals(articleAuthor)) {
            throw new NoAuthorityException();
        }
    }

    private String uploadFile(int targetId, MultipartFile file) throws IOException {
        // "articles/{articleId}/{originalFileName}"
        String originalFilename = file.getOriginalFilename();
        String key = "articles/" + targetId + "/" + originalFilename;
        return storageService.uploadFile(key, file);
    }

}
