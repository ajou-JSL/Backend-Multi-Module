package jsl.moum.global.error.exception;

import jsl.moum.global.error.ErrorCode;

public class NeedLoginException extends CustomException{
    public NeedLoginException() {
        super(ErrorCode.NEED_LOGIN);
    }
}
