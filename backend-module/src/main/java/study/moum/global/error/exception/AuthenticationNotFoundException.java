package study.moum.global.error.exception;


import study.moum.global.error.ErrorCode;

public class AuthenticationNotFoundException extends CustomException {
    public AuthenticationNotFoundException() {
        super(ErrorCode.AUTHENTICATION_NOT_FOUND);
    }
}