package jsl.moum.chatappmodule.global.error.exception;


import jsl.moum.chatappmodule.global.error.ErrorCode;

public class MemberNotExistException extends CustomException {
    public MemberNotExistException(){super(ErrorCode.MEMBER_NOT_EXIST);}
}
