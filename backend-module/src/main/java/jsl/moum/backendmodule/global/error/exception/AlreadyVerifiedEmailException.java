package jsl.moum.backendmodule.global.error.exception;


import jsl.moum.backendmodule.global.error.ErrorCode;

public class AlreadyVerifiedEmailException extends CustomException{
    public AlreadyVerifiedEmailException(){super(ErrorCode.EMAIL_ALREADY_VERIFIED);}
}
