package jsl.moum.chatappmodule.global.error.exception;

import jsl.moum.chatappmodule.global.error.ErrorCode;

public class DuplicateUsernameException extends CustomException {
    public DuplicateUsernameException() {
        super(ErrorCode.USER_NAME_ALREADY_EXISTS);
    }
}
