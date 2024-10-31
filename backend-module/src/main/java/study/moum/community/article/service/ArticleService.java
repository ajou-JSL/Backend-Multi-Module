package study.moum.community.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.domain.repository.MemberRepository;
import study.moum.community.article.domain.article.ArticleEntity;
import study.moum.community.article.domain.article.ArticleRepository;
import study.moum.community.article.domain.article_details.ArticleDetailsEntity;
import study.moum.community.article.domain.article_details.ArticleDetailsRepository;
import study.moum.community.article.domain.article_details.ArticleRepositoryCustom;
import study.moum.community.article.dto.ArticleDetailsDto;
import study.moum.community.article.dto.ArticleDto;
import study.moum.objectstorage.StorageService;
import study.moum.global.error.ErrorCode;
import study.moum.global.error.exception.CustomException;
import study.moum.global.error.exception.NeedLoginException;
import study.moum.global.error.exception.NoAuthorityException;

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
    private final ArticleRepositoryCustom articleRepositoryCustom;
    private final StorageService storageService;

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    /**
     * 게시글 작성 메서드.
     *
     * @param articleRequestDto 게시글 작성 요청 DTO
     * @param memberName 작성자 사용자 이름
     * @param file 멀티파트 파일
     * @return 작성된 게시글의 응답 DTO
     *
     * 해당 메서드는 사용자가 게시글을 작성할 때 호출되며, 작성자의 정보를 확인하고 게시글과
     * 게시글 상세 정보를 저장한 후 이를 반환
     *
     * - 작성자 확인 후 게시글 저장
     * - 게시글의 상세 내용(ArticleDetails)도 함께 저장
     */
    @Transactional
    public ArticleDto.Response postArticle(ArticleDto.Request articleRequestDto, MultipartFile file, String memberName) throws IOException {
        MemberEntity author = memberRepository.findByUsername(memberName);
        if(author == null){
            throw new NeedLoginException();
        }

        // article 테이블 -> title 작성
        ArticleDto.Request articleRequest = ArticleDto.Request.builder()
                .author(author)
                .title(articleRequestDto.getTitle())
                .category(articleRequestDto.getCategory())
                .build();

        // article 테이블 저장
        ArticleEntity newArticle = articleRequest.toEntity();
        articleRepository.save(newArticle);

        // 파일 업로드 후 URL 획득
        // S3에 저장할 파일의 키를 설정 (예: "articles/{articleId}/{originalFileName}")
        String originalFilename = file.getOriginalFilename();
        String key = "articles/" + newArticle.getId() + "/" + originalFilename; // 키 생성
        String fileUrl = storageService.uploadFile(key, file); // 업로드 메서드 호출

        // article_details 테이블 -> content 작성
        ArticleDetailsDto.Request articleDetailsRequestDto = ArticleDetailsDto.Request.builder()
                .content(articleRequestDto.getContent())
                .fileUrl(fileUrl) // 파일 URL 설정
                .build();

        // article_details 테이블 저장
        ArticleDetailsEntity newArticleDetails = articleDetailsRequestDto.toEntity();
        articleDetailsRepository.save(newArticleDetails);

        return new ArticleDto.Response(newArticle);

    }

    /**
     * 게시글 조회 메서드.
     *
     * @param articleDetailsId 게시글 상세 정보의 ID
     * @return 게시글의 상세 내용과 게시글 정보를 담은 응답 DTO
     *
     * 게시글과 게시글 상세 정보를 조회하고, 조회수가 증가한 후 반환
     */
    @Transactional
    public ArticleDetailsDto.Response getArticleById(int articleDetailsId){
        ArticleDetailsEntity articleDetails = getArticleDetails(articleDetailsId);
        ArticleEntity article = getArticle(articleDetailsId);

        article.viewCountUp(); // 조회수 증가

        return new ArticleDetailsDto.Response(articleDetails, article);
    }

    /**
     * 게시글 목록 조회 메서드.
     *
     * @return 게시글 목록의 응답 DTO 리스트
     *
     * 데이터베이스에서 모든 게시글을 조회한 후, 각 게시글을 응답 DTO로 변환하여 리스트로 반환
     */
    @Transactional(readOnly = true)
    public List<ArticleDto.Response> getArticleList(int page, int size) {
        // 데이터베이스에서 모든 게시글 조회
        List<ArticleEntity> articles = articleRepository.findAll(PageRequest.of(page, size)).getContent();

        // 조회된 게시글들을 DTO로 변환
        List<ArticleDto.Response> articleResponseList = articles.stream()
                .map(ArticleDto.Response::new)
                .collect(Collectors.toList());

        return articleResponseList;
    }

    /**
     * 게시글 수정 메서드.
     *
     * @param articleDetailsId 게시글 상세 정보의 ID
     * @param articleDetailsRequestDto 수정할 게시글 상세 내용 DTO
     * @param memberName 수정 요청한 사용자 이름
     * @return 수정된 게시글과 게시글 상세 정보를 담은 응답 DTO
     *
     * 게시글 작성자만 수정할 수 있으며, 제목과 내용을 수정한 후 이를 저장
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

        // 새로 요청된 수정 값들 추출
        String newTitle = articleDetailsRequestDto.getTitle();
        String newContent = articleDetailsRequestDto.getContent();
        ArticleEntity.ArticleCategories newCategory = articleDetailsRequestDto.getCategory();

        // 기존 파일 URL
        String existingFileUrl = articleDetails.getFileUrl();

        // 새로운 파일이 있을 경우 처리
        if (file != null && !file.isEmpty()) {
            // S3에서 기존 파일 삭제
            if (existingFileUrl != null && !existingFileUrl.isEmpty()) {
                String existingFileName = existingFileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
                storageService.deleteFile(existingFileName); // S3에서 기존 파일 삭제
            }

            // 새로운 파일 업로드
            String newFileName = "articles/" + articleDetailsId + "/" + file.getOriginalFilename(); // 폴더 구조에 맞게 설정
            String newFileUrl = storageService.uploadFile(newFileName, file); // S3에 파일 업로드
            articleDetails.updateArticleImage(newFileUrl);
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
     * 게시글 삭제 메서드.
     *
     * @param articleDetailsId 게시글 상세 정보의 ID
     * @param memberName 삭제 요청한 사용자 이름
     * @return 삭제된 게시글의 응답 DTO
     *
     * 게시글 작성자만 삭제할 수 있으며, 게시글과 게시글 상세 정보를 삭제
     */
    @Transactional
    public ArticleDto.Response deleteArticleDetails(int articleDetailsId, String memberName){

        ArticleDetailsEntity articleDetails = getArticleDetails(articleDetailsId);
        ArticleEntity article = getArticle(articleDetailsId);

        // 로그인유저 == 작성자 여부 체크
        String articleAuthor = article.getAuthor().getUsername();
        checkAuthor(memberName, articleAuthor);

        String fileUrl = articleDetails.getFileUrl();
        if (fileUrl != null && !fileUrl.isEmpty()) {
            // S3에서의 파일 경로 제거해서 key 추출하기 -> 경로 : /articles/{id}/{fileName} 이런식임
            String fileName = fileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8); // URL 디코딩
            storageService.deleteFile(fileName); // S3에서 파일 삭제
        }
        // article_details, article 테이블 둘 다 삭제
        articleDetailsRepository.deleteById(articleDetailsId);
        articleRepository.deleteById(articleDetailsId);

        return new ArticleDto.Response(article);
    }

    /**
     * 카테고리에 따른 게시글 목록 조회 메서드
     *
     * @param category 게시글 카테고리
     * @return 카테고리에 해당하는 게시글 리스트
     */
    @Transactional(readOnly = true)
    public List<ArticleDto.Response> getArticlesByCategory(ArticleEntity.ArticleCategories category, int page, int size) {
        List<ArticleEntity> articles;

        if (category == ArticleEntity.ArticleCategories.FREE_TALKING_BOARD) {
            articles = articleRepositoryCustom.findFreeTalkingArticles(page, size);
        } else if (category == ArticleEntity.ArticleCategories.RECRUIT_BOARD) {
            articles = articleRepositoryCustom.findRecruitingdArticles(page, size);
        } else {
            throw new CustomException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        // 조회된 게시글들을 DTO로 변환
        return articles.stream()
                .map(ArticleDto.Response::new)
                .collect(Collectors.toList());
    }

    /**
     * 주어진 키워드를 사용하여 게시글을 검색하는 메서드.
     *
     * @param keyword 검색어
     * @return 검색된 게시글 리스트
     */
    @Transactional(readOnly = true)
    public List<ArticleDto.Response> getArticleWithTitleSearch(String keyword, String category,int page, int size) {
        List<ArticleEntity> articles = articleRepositoryCustom.searchArticlesByTitleKeyword(keyword, category, page, size);

        // 조회된 게시글들을 DTO로 변환
        List<ArticleDto.Response> articleResponseList = articles.stream()
                .map(ArticleDto.Response::new)
                .collect(Collectors.toList());

        return articleResponseList;
    }

    /**
     * 주어진 키워드를 사용하여 게시글을 검색하는 메서드.
     *
     * @param memberName 사용자 이름
     * @param page
     * @param size
     * @return 검색된 게시글 리스트
     */
    @Transactional(readOnly = true)
    public List<ArticleDto.Response> getMyWishlist(String memberName,int page, int size) {
//        List<ArticleEntity> articles = articleRepositoryCustom.searchArticlesByTitleKeyword(keyword, category, page, size);
        int memberId = memberRepository.findByUsername(memberName).getId();

        List<ArticleEntity> articles = articleRepository.findLikedArticles(memberId);

        // 조회된 게시글들을 DTO로 변환
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

}
