package jsl.moum.community.pamphlet.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jsl.moum.community.pamphlet.dto.PamphletDto;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.community.perform.domain.repository.PerformArticleRepository;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.objectstorage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PamphletService {

    private final String PAMPHLET_BASE_URL = "http://223.130.162.175:8080/public/pamphlet";
    private final String PAMPHLET_LOCAL_TEST_BASE_URL = "http://localhost:8080/public/pamphlet";
    private final Integer QR_WIDTH = 250;
    private final Integer QR_HEIGHT = 250;

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    private final PerformArticleRepository performArticleRepository;
    private final StorageService storageService;

    public PamphletDto getPerformArticleById(int performArticleId){
        PerformArticleEntity target = performArticleRepository.findById(performArticleId)
                .orElseThrow(()-> new CustomException(ErrorCode.PERFORM_ARTICLE_NOT_FOUND));

        return new PamphletDto(target);
    }

    public String getQrBaseUrl (Integer performArticleId) {
        return PAMPHLET_BASE_URL + "/" + performArticleId;
    }

    public String getQrBaseUrlLocalhost (Integer performArticleId) {
        return PAMPHLET_LOCAL_TEST_BASE_URL + "/" + performArticleId;
    }


    /**
     *
     * QR Code APIs
     *
     */

    public String getQRCodeUrl(int performArticleId) {
        log.info("getQRCodeUrl : {}", performArticleId);

        String qrName = performArticleId + ".png";
        String existingFileUrl = "https://kr.object.ncloudstorage.com/" + bucket + "/qr/" + qrName;
        return existingFileUrl;
    }

    public byte[] generateQRCode(int performArticleId) {
        log.info("generateQRCode : {}", performArticleId);
        if(!performArticleRepository.existsById(performArticleId)){
            throw new CustomException(ErrorCode.PERFORM_ARTICLE_NOT_FOUND);
        }
        String url = getQrBaseUrl(performArticleId);

        byte[] qrCodeByteArray = generateQRCodeByteArray(url);
        return qrCodeByteArray;
    }


    public byte[] generateQRCodeLocalhost(int performArticleId) {
        log.info("generateQRCodeLocalhost : {}", performArticleId);
        if(!performArticleRepository.existsById(performArticleId)){
            throw new CustomException(ErrorCode.PERFORM_ARTICLE_NOT_FOUND);
        }
        String url = getQrBaseUrlLocalhost(performArticleId);

        byte[] qrCodeByteArray = generateQRCodeByteArray(url);
        return qrCodeByteArray;
    }

    public String saveQRCodeImage(int performArticleId, byte[] qrCodeByteArray) {
        log.info("saveQRCodeImage : {}", performArticleId);

        // Save qrCodeImage to S3, and return fileUrl
        try {
            String fileUrl = "qr/" + performArticleId + ".png";

            ByteArrayInputStream qrCodeStream = new ByteArrayInputStream(qrCodeByteArray);
            BufferedImage qrCodeBufferedImage = ImageIO.read(qrCodeStream);
            File qrCodeImageFile = File.createTempFile("image", ".png");

            if(!ImageIO.write(qrCodeBufferedImage, "png", qrCodeImageFile)){
                throw new CustomException(ErrorCode.QR_GENERATE_FAIL);
            }

            // Upload the file to S3
            String newFileUrl = storageService.uploadFile(fileUrl, qrCodeImageFile, "image/png");
            qrCodeImageFile.delete();
            return newFileUrl;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.QR_GENERATE_FAIL);
        }
    }

    public void deleteQRCodeImage(int performArticleId) {
        log.info("deleteQRCodeImage : {}", performArticleId);
        String fileUrl = "qr/" + performArticleId + ".png";
        storageService.deleteFile(fileUrl);
    }

    private byte[] generateQRCodeByteArray(String url) {
        log.info("generateQRCodeByteArray : {}", url);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (WriterException e) {
            throw new CustomException(ErrorCode.QR_GENERATE_FAIL);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.QR_GENERATE_FAIL);
        }
    }


}
