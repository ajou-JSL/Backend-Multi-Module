package jsl.moum.community.article.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.community.article.domain.article.ArticleRepositoryCustom;
import jsl.moum.community.article.domain.article_details.ArticleDetailsEntity;
import jsl.moum.community.article.domain.article_details.ArticleDetailsRepositoryCustom;
import jsl.moum.community.article.dto.ArticleDetailsDto;
import jsl.moum.community.article.dto.ArticleDto;
import jsl.moum.community.article.dto.UpdateArticleDto;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.objectstorage.StorageService;
import jsl.moum.report.domain.ArticleReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleDetailsRepository articleDetailsRepository;
    private final MemberRepository memberRepository;
    private final ArticleDetailsRepositoryCustom articleDetailsRepositoryCustom;
    private final StorageService storageService;
    private final ArticleRepositoryCustom articleRepositoryCustom;

    private final ObjectMapper objectMapper;
    private final ArticleReportRepository articleReportRepository;

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    /**
     * 게시글 작성(생성)
     */
    @Transactional
    public ArticleDto.Response postArticle(ArticleDto.Request articleRequestDto, List<MultipartFile> files, String memberName) throws IOException {
        MemberEntity author = memberRepository.findByUsername(memberName);

        // article 테이블 -> title 작성
        ArticleDto.Request articleRequest = ArticleDto.Request.builder()
                .author(author)
                .title(articleRequestDto.getTitle())
                .category(articleRequestDto.getCategory())
                .genre(articleRequestDto.getGenre())
                .build();

        ArticleEntity newArticle = articleRequest.toEntity();
        articleRepository.save(newArticle);

        List<String> newUrls = uploadFiles(files, newArticle.getId());

        ArticleDetailsDto.Request articleDetailsRequestDto = ArticleDetailsDto.Request.builder()
                .articleId(newArticle.getId())
                .content(articleRequestDto.getContent())
                .fileUrls(newUrls)
                .build();

        ArticleDetailsEntity newArticleDetails = articleDetailsRequestDto.toEntity();
        articleDetailsRepository.save(newArticleDetails);

        newArticle.setImageUrl(newUrls.get(0));
        author.updateMemberExpAndRank(1);

        return new ArticleDto.Response(newArticle);
    }

    /**
     * 게시글 조회
     */
    @Transactional
    public ArticleDetailsDto.Response getArticleById(int articleId, String loginUserName){
        ArticleEntity article = getArticle(articleId);
        ArticleDetailsEntity articleDetails = getArticleDetails(articleId);

        if(!loginUserName.equals(article.getAuthor().getUsername())){
            article.viewCountUp(); // 조회수 증가
        }

        return new ArticleDetailsDto.Response(articleDetails, article);
    }

    /**
     * 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ArticleDto.Response> getArticleList(int page, int size) {

        List<ArticleEntity> articles = articleRepository
                .findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent();

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
                                                           List<MultipartFile> files,
                                                           String memberName) throws IOException {

        ArticleDetailsEntity articleDetails = getArticleDetails(articleDetailsId);
        ArticleEntity article = getArticle(articleDetailsId);
        String articleAuthor = article.getAuthor().getUsername();
        UpdateArticleDto.Request updateArticleDto = UpdateArticleDto.Request.builder()
                .genre(articleDetailsRequestDto.getGenre())
                .title(articleDetailsRequestDto.getTitle())
                .category(articleDetailsRequestDto.getCategory())
                .fileUrl(articleDetailsRequestDto.getFileUrls().get(0))
                .build();

        // 로그인유저 == 작성자 여부 체크
        checkAuthor(memberName, articleAuthor);

        String newTitle = articleDetailsRequestDto.getTitle();
        String newContent = articleDetailsRequestDto.getContent();
        ArticleEntity.ArticleCategories newCategory = articleDetailsRequestDto.getCategory();

        List<String> existingFileUrls = articleDetails.getImageUrls();
        if(files == null){
            throw new CustomException(ErrorCode.FILE_UPDATE_FAIL);
        } else if(files.get(0).getSize() != 0){
            log.info("if(files.get(0).getSize() != 0 || files != null)");
            deleteExistingFiles(existingFileUrls);
            List<String> newFileUrls = uploadFiles(files, articleDetailsId);
            articleDetails.updateFileUrls(newFileUrls);
        }

        // article_details, article 둘 다 update
        articleDetails.updateArticleDetails(newContent);
        article.updateArticle(updateArticleDto);

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

        List<String> existingFileUrls = articleDetails.getImageUrls();
        deleteExistingFiles(existingFileUrls);

        // 관련 article_reports 삭제
        // article_details, article 테이블 둘 다 삭제
        articleReportRepository.deleteAll(article.getArticleReports());
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
        Pageable pageable = PageRequest.of(page,size);
        Page<ArticleEntity> articles = articleRepositoryCustom.getAllHotArticles(pageable);

        return articles.stream()
                .map(ArticleDto.Response::new)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리에 따른 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ArticleDto.Response> getArticlesByCategory(ArticleEntity.ArticleCategories category, int page, int size) {
        Page<ArticleEntity> articles;

        Pageable pageable = PageRequest.of(page,size);
        if (category == ArticleEntity.ArticleCategories.FREE_TALKING_BOARD) {
            articles = articleDetailsRepositoryCustom.findFreeTalkingArticles(pageable);
        } else if (category == ArticleEntity.ArticleCategories.RECRUIT_BOARD) {
            articles = articleDetailsRepositoryCustom.findRecruitingArticles(pageable);
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
        Pageable pageable = PageRequest.of(page,size);
        Page<ArticleEntity> articles = articleDetailsRepositoryCustom.searchArticlesByTitleKeyword(keyword, category, pageable);

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

        Pageable pageable = PageRequest.of(page,size);
        Page<ArticleEntity> articles = articleRepositoryCustom.findLikedArticlesByMember(memberId, pageable);

        List<ArticleDto.Response> articleResponseList = articles.stream()
                .map(ArticleDto.Response::new)
                .collect(Collectors.toList());

        return articleResponseList;
    }

    /**
     * 필터링으로 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ArticleDto.Response> getArticlesByFiltering(ArticleDto.SearchDto searchDto, int page, int size) {

//        ArticleDto.SearchDto searchDto = null;
//        log.info("encodedString : {}", encodedString);
//        if (encodedString != null) {
//            try {
//                log.info("try 진입");
//                String decodedString = new String(Base64.getDecoder().decode(encodedString));
//                log.info("decodedString : {}", decodedString);
//                searchDto = objectMapper.readValue(decodedString, ArticleDto.SearchDto.class);
//                log.info("searchDto : {}", searchDto);
//            } catch (IllegalArgumentException |JsonProcessingException e) {
//                log.error(e.getMessage());
//                 throw new CustomException(ErrorCode.BASE64_PROCESS_FAIL);
//            }
//        }
        Pageable pageable = PageRequest.of(page,size);
        Page<ArticleEntity> articles = articleRepositoryCustom.searchArticlesWithFiltering(searchDto, pageable);

        Page<ArticleDto.Response> articleResponseList = articles
                .map(ArticleDto.Response::new);

        return articleResponseList;
    }


    private ArticleDetailsEntity getArticleDetails(int articleId) {
        return articleDetailsRepositoryCustom.findArticleDetailsByArticleId(articleId);
                /*.orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_DETAILS_NOT_FOUND));*/
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


    private List<String> uploadFiles(List<MultipartFile> files, int targetId) throws IOException {
        List<String> newFileUrls = new ArrayList<>();

        // "articles/{articleId}/{originalFileName}"
        if (files != null && !files.isEmpty() && files.size() != 0) {
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                String key = "articles/" + targetId + "/" + originalFilename;
                String fileUrl = storageService.uploadImage(key, file);
                log.info("================= 파일 리스트 업로드");
                newFileUrls.add(fileUrl);
            }
        }
        return newFileUrls;
    }

    private void deleteExistingFiles(List<String> existingFileUrls) {
        if(existingFileUrls == null || existingFileUrls.isEmpty()){
            existingFileUrls = new ArrayList<>();
        }
        for (String existingFileUrl : existingFileUrls) {
            String existingFileName = existingFileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
            log.info("================= 기존 파일 리스트 삭제");
            storageService.deleteFile(existingFileName);
        }
    }

}
