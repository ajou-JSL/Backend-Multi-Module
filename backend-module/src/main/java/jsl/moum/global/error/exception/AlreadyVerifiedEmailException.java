package jsl.moum.global.error.exception;


import jsl.moum.global.error.ErrorCode;

public class AlreadyVerifiedEmailException extends CustomException{
    public AlreadyVerifiedEmailException(){super(ErrorCode.EMAIL_ALREADY_VERIFIED);}
}
