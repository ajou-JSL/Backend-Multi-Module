package jsl.moum.chatappmodule.global.error.exception;

import jsl.moum.chatappmodule.global.error.ErrorCode;

public class NoAuthorityException extends CustomException {
    public NoAuthorityException() {
        super(ErrorCode.NO_AUTHORITY);
    }
}
