package jsl.moum.objectstorage;

import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final S3Client s3Client;
    private final List<String> allowedImageTypes = List.of("image/jpeg", "image/png", "image/gif", "image/bmp", "image/tiff", "image/webp", "image/heic");

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    /**
     * S3에 업로드
     */
    public String uploadFile(String key, MultipartFile multipartFile) throws IOException {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(multipartFile.getContentType())
                        .acl("public-read")
                        .build(),
                RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize())
        );
        // NCP에서 파일 URL 반환
        return "https://kr.object.ncloudstorage.com/" + bucket + "/" + key;
    }

    public String uploadImage(String key, MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }
        if(!isValidImageType(multipartFile)){
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }
        String fileUrl = uploadFile(key, multipartFile);
        return fileUrl;
    }

    /**
     * S3에 QR 이미지 업로드 전용
     */
    public String uploadFile(String key, File file, String contentType) throws IOException {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType) // Automatically determine content type
                        .acl("public-read")
                        .build(),
                RequestBody.fromFile(file)
        );
        // NCP에서 파일 URL 반환
        return "https://kr.object.ncloudstorage.com/" + bucket + "/" + key;
    }

    /**
     * S3에 여러 파일 업로드
     */
    public List<String> uploadFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String fileUrl = uploadFile(key, file);
            fileUrls.add(fileUrl);
        }

        return fileUrls;
    }

    /**
     * S3에서 파일 다운로드
     */
    public byte[] downloadFile(String key) throws IOException {
        return s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        ).readAllBytes();
    }

    /**
     * S3에서 파일 삭제
     */
    public void deleteFile(String key) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        );
    }

    private boolean isValidImageType(MultipartFile file){
        Tika tika = new Tika();
        try {
            String detectedMimeType = tika.detect(file.getInputStream()).toLowerCase();
            return allowedImageTypes.contains(detectedMimeType);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }
    }
}