package jsl.moum.backendmodule.global.error.exception;


import jsl.moum.backendmodule.global.error.ErrorCode;

public class MemberNotExistException extends CustomException{
    public MemberNotExistException(){super(ErrorCode.MEMBER_NOT_EXIST);}
}
