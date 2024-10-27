package jsl.moum.backendmodule.global.error.exception;

import jsl.moum.backendmodule.global.error.ErrorCode;

public class NoAuthorityException extends CustomException{
    public NoAuthorityException() {
        super(ErrorCode.NO_AUTHORITY);
    }
}
