package jsl.moum.community.pamphlet.controller;

import jsl.moum.community.pamphlet.dto.PamphletDto;
import jsl.moum.community.pamphlet.service.PamphletService;
import jsl.moum.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PamphletController {

    private final PamphletService pamphletService;

    @PostMapping("/api/pamphlet/qr-code")
    public ResponseEntity<byte[]> createQrCode(@RequestParam(name = "id") int id) {
        byte[] qrCodeImage = pamphletService.generateQRCode(id);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "image/png");
        return new ResponseEntity<>(qrCodeImage, headers, 201);
    }

    // Test API for localhost address
    @PostMapping("/api/pamphlet/qr-code/localhost")
    public ResponseEntity<byte[]> createQrCodeLocalhost(@RequestParam(name = "id") int id) {
        byte[] qrCodeImage = pamphletService.generateQRCodeImageLocalhost(id);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "image/png");
        return new ResponseEntity<>(qrCodeImage, headers, 201);
    }

    @GetMapping("/api/pamphlet/qr-code/{id}")
    public ResponseEntity<byte[]> getQrCode(@PathVariable(name = "id") int id) {

        return null;
    }

    @DeleteMapping("/api/pamphlet/{id}")
    public ResponseEntity<ResultResponse> deleteQrCode(@PathVariable(name = "id") int id) {

        return null;
    }

    @GetMapping("/public/pamphlet/{id}")
    public String getPamphlet(@PathVariable(name = "id") int id, Model model) {
        PamphletDto pamphletDto = pamphletService.getPerformArticleById(id);

        // Add performArticle to model
        model.addAttribute("pamphlet", pamphletDto);
        return "pamphletView";
    }

}
