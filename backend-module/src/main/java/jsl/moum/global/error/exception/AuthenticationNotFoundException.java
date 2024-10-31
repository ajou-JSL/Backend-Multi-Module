package jsl.moum.global.error.exception;


import jsl.moum.global.error.ErrorCode;

public class AuthenticationNotFoundException extends CustomException {
    public AuthenticationNotFoundException() {
        super(ErrorCode.AUTHENTICATION_NOT_FOUND);
    }
}