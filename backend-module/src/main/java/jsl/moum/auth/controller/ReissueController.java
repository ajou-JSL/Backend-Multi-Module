package jsl.moum.auth.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import jsl.moum.auth.service.ReissueService;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.ErrorResponse;
import jsl.moum.global.response.ResultResponse;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            ResultResponse resultResponse = reissueService.reissue(request, response);
            return new ResponseEntity<>(resultResponse, HttpStatus.valueOf(resultResponse.getStatus()));
        } catch (ExpiredJwtException e) {
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.JWT_TOKEN_EXPIRED);
            return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.REFRESH_TOKEN_INVALID);
            return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
        }
    }
}
