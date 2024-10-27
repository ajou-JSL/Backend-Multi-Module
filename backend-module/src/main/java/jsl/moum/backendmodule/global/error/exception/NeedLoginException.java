package jsl.moum.backendmodule.global.error.exception;

import jsl.moum.backendmodule.global.error.ErrorCode;

public class NeedLoginException extends CustomException{
    public NeedLoginException() {
        super(ErrorCode.NEED_LOGIN);
    }
}
