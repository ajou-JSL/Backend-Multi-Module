package jsl.moum.global.error.exception;

import jsl.moum.global.error.ErrorCode;

public class EmailVerificationException extends CustomException{
    public EmailVerificationException() {
        super(ErrorCode.EMAIL_VERIFY_FAILED);
    }
}
