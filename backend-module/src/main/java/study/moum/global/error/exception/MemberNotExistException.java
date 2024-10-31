package study.moum.global.error.exception;


import study.moum.global.error.ErrorCode;

public class MemberNotExistException extends CustomException{
    public MemberNotExistException(){super(ErrorCode.MEMBER_NOT_EXIST);}
}
