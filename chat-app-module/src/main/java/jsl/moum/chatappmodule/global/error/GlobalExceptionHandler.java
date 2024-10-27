package jsl.moum.chatappmodule.global.error;


import jsl.moum.chatappmodule.global.error.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import reactor.core.publisher.Mono;

import static jsl.moum.chatappmodule.global.error.ErrorCode.*;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Reactive 예외처리 핸들러에서는 Mono객체를 반환해야 한다
     * 그렇지 않으면 reactive에서는 존재하지 않는 jakarta.servlet.http.HttpServletResponse 객체를 사용하려고 하기 때문에 에러가 발생한다
     * @param e
     * @return Mono<ResponseEntity<ErrorResponse>>
     */

//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponse> handleBadCredentialException(BadCredentialsException e) {
//        final ErrorResponse response = ErrorResponse.of(BAD_CREDENTIALS);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    protected ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
//        final ErrorResponse response = ErrorResponse.of(DUPLICATE_LIKES);
//        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
//        final ErrorResponse response = ErrorResponse.of(e);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
//        final ErrorResponse response = ErrorResponse.of(METHOD_NOT_ALLOWED);
//        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
//    }
//
//    // @Valid, @Validated 에서 binding error 발생 시 (@RequestBody)
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
//        final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE, e.getBindingResult());
//        System.out.println("==================  @Valid, @Validated 에서 binding error 발생 시 (@RequestBody)");
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    // 비즈니스 요구사항에 따른 Exception
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponse> handleBusinessException(CustomException e) {
//        final ErrorCode errorCode = e.getErrorCode();
//        final ErrorResponse response = ErrorResponse.of(errorCode, e.getErrors());
//        System.out.println("================== 비즈니스 요구사항에 따른 Exception");
//        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
//    }

    // 그 밖에 발생하는 모든 예외처리가 이곳으로 모인다.
    @ExceptionHandler(Exception.class)
    protected Mono<ResponseEntity<ErrorResponse>> handleException(Exception e) {
        log.error("Exception: ", e);
        final ErrorResponse response = ErrorResponse.of(INTERNAL_SERVER_ERROR);
        return Mono.just(new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR));
    }

}