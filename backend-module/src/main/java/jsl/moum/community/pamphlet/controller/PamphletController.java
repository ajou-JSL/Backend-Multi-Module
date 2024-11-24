package jsl.moum.community.pamphlet.controller;

import jsl.moum.community.pamphlet.dto.PamphletDto;
import jsl.moum.community.pamphlet.service.PamphletService;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PamphletController {

    private final PamphletService pamphletService;

    @PostMapping("/api/pamphlet/qr-code")
    public ResponseEntity<ResultResponse> createQrCode(@RequestParam(name = "id") int id) {
        byte[] qrCodeByteArray = pamphletService.generateQRCode(id);
        String qrUrl = pamphletService.saveQRCodeImage(id, qrCodeByteArray);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.QR_GENERATE_SUCCESS, qrUrl);
        return ResponseEntity.ok(resultResponse);
    }

    @GetMapping("/api/pamphlet/qr-code/{id}")
    public ResponseEntity<ResultResponse> getQrCode(@PathVariable(name = "id") int id) {
        String qrUrl = pamphletService.getQRCodeUrl(id);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.QR_GENERATE_SUCCESS, qrUrl);
        return ResponseEntity.ok(resultResponse);
    }

    @DeleteMapping("/api/pamphlet/qr-code")
    public ResponseEntity<ResultResponse> deleteQrCode(@RequestParam(name = "id") int id) {
        pamphletService.deleteQRCodeImage(id);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.QR_DELETE_SUCCESS, null);
        return ResponseEntity.ok(resultResponse);
    }

    @GetMapping("/public/pamphlet/{id}")
    public String getPamphlet(@PathVariable(name = "id") int id, Model model) {
        PamphletDto pamphletDto = pamphletService.getPerformArticleById(id);

        // Add performArticle to model
        model.addAttribute("pamphlet", pamphletDto);
        return "pamphletView";
    }

    /**
     *
     * Test API
     *
     */

    // Test API for localhost address
    @PostMapping("/api/pamphlet/qr-code/localhost")
    public ResponseEntity<byte[]> createQrByteArrayLocalhost(@RequestParam(name = "id") int id) {
        byte[] qrCodeByteArray = pamphletService.generateQRCodeLocalhost(id);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "image/png");
        return new ResponseEntity<>(qrCodeByteArray, headers, 201);
    }
}
