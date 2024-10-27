package jsl.moum.chatappmodule.global.error.exception;


import jsl.moum.chatappmodule.global.error.ErrorCode;

public class AlreadyVerifiedEmailException extends CustomException {
    public AlreadyVerifiedEmailException(){super(ErrorCode.EMAIL_ALREADY_VERIFIED);}
}
