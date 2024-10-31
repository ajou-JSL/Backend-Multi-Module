package study.moum.global.error.exception;


import study.moum.global.error.ErrorCode;

public class AlreadyVerifiedEmailException extends CustomException{
    public AlreadyVerifiedEmailException(){super(ErrorCode.EMAIL_ALREADY_VERIFIED);}
}
