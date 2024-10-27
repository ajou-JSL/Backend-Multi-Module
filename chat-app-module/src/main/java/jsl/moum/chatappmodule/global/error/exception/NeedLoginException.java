package jsl.moum.chatappmodule.global.error.exception;

import jsl.moum.chatappmodule.global.error.ErrorCode;

public class NeedLoginException extends CustomException {
    public NeedLoginException() {
        super(ErrorCode.NEED_LOGIN);
    }
}
