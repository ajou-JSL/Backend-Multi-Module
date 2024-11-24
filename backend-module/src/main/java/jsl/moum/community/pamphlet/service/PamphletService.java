package jsl.moum.community.pamphlet.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jsl.moum.community.pamphlet.dto.PamphletDto;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.community.perform.domain.repository.PerformArticleRepository;
import jsl.moum.community.perform.dto.PerformArticleDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PamphletService {

    private final String PAMPHLET_BASE_URL = "http://223.130.162.175:8080/pamphlet";
    private final String PAMPHLET_LOCAL_TEST_BASE_URL = "http://localhost:8080/pamphlet";


    private final PerformArticleRepository performArticleRepository;

    public PamphletDto getPerformArticleById(int performArticleId){
        PerformArticleEntity target = performArticleRepository.findById(performArticleId)
                .orElseThrow(()-> new CustomException(ErrorCode.ILLEGAL_ARGUMENT));

        return new PamphletDto(target);
    }


    /**
     *
     * QR Code APIs
     *
     */

    public byte[] generateQRCode(int performArticleId) {
        log.info("generateQRCodeImage : {}", performArticleId);
        String url = PAMPHLET_BASE_URL + "/" + performArticleId;

        byte[] qrCodeImage = generateQRCodeImage(url);
        return qrCodeImage;
    }

    public byte[] generateQRCodeImageLocalhost(int performArticleId) {
        log.info("generateQRCodeImageLocalhost : {}", performArticleId);
        String url = PAMPHLET_LOCAL_TEST_BASE_URL + "/" + performArticleId;

        byte[] qrCodeImage = generateQRCodeImage(url);
        return qrCodeImage;
    }

    public void saveQRCodeImage(int performArticleId, byte[] qrCodeImage) {
        log.info("saveQRCodeImage : {}", performArticleId);
        // Save qrCodeImage to S3

    }

    public byte[] getQRCodeImage(int performArticleId) {
        log.info("getQRCodeImage : {}", performArticleId);
        // Get qrCodeImage from S3
        return null;
    }

    private byte[] generateQRCodeImage(String url) {
        log.info("generateQRCodeImage : {}", url);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 250, 250);

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
