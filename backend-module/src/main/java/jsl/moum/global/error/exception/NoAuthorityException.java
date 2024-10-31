package jsl.moum.global.error.exception;

import jsl.moum.global.error.ErrorCode;

public class NoAuthorityException extends CustomException{
    public NoAuthorityException() {
        super(ErrorCode.NO_AUTHORITY);
    }
}
