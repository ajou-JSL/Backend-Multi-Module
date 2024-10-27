package jsl.moum.chatappmodule.global.error.exception;

import jsl.moum.chatappmodule.global.error.ErrorCode;

public class EmailVerificationException extends CustomException {
    public EmailVerificationException() {
        super(ErrorCode.EMAIL_VERIFY_FAILED);
    }
}
