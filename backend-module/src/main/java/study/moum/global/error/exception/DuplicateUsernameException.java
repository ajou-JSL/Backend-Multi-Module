package study.moum.global.error.exception;

import study.moum.global.error.ErrorCode;

public class DuplicateUsernameException extends CustomException {
    public DuplicateUsernameException() {
        super(ErrorCode.USER_NAME_ALREADY_EXISTS);
    }
}
