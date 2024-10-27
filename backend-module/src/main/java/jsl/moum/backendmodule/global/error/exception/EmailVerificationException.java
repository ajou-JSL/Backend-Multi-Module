package jsl.moum.backendmodule.global.error.exception;

import jsl.moum.backendmodule.global.error.ErrorCode;

public class EmailVerificationException extends CustomException{
    public EmailVerificationException() {
        super(ErrorCode.EMAIL_VERIFY_FAILED);
    }
}
