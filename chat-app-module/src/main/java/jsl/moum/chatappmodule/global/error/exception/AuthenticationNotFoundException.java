package jsl.moum.chatappmodule.global.error.exception;


import jsl.moum.chatappmodule.global.error.ErrorCode;

public class AuthenticationNotFoundException extends CustomException {
    public AuthenticationNotFoundException() {
        super(ErrorCode.AUTHENTICATION_NOT_FOUND);
    }
}