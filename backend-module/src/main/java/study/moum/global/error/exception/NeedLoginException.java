package study.moum.global.error.exception;

import study.moum.global.error.ErrorCode;

public class NeedLoginException extends CustomException{
    public NeedLoginException() {
        super(ErrorCode.NEED_LOGIN);
    }
}
