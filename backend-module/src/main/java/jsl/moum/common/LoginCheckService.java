package jsl.moum.common;

import jsl.moum.global.error.exception.NeedLoginException;
import org.springframework.stereotype.Service;

@Service
public class LoginCheckService {
    public String loginCheck(String username){
        if(username == null){
            throw new NeedLoginException();
        }

        return username;
    }
}
