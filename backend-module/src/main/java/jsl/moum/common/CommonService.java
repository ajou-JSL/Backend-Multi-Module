package jsl.moum.common;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.global.error.exception.NeedLoginException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final MemberRepository memberRepository;

    public String loginCheck(String username){
        if(username == null){
            throw new NeedLoginException();
        }

        return username;
    }

    public MemberEntity findMemberByUsername(String username){
        MemberEntity member = memberRepository.findByUsername(username);
        if(member == null){
            throw new CustomException(ErrorCode.MEMBER_NOT_EXIST);
        }
        return member;
    }
}
