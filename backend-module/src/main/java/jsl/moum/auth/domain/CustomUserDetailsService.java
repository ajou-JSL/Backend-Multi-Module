package jsl.moum.auth.domain;

import jsl.moum.auth.domain.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jsl.moum.auth.domain.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberEntity memberData = memberRepository.findByUsername(username);

        if (memberData != null) {
            CustomUserDetails customUserDetails = new CustomUserDetails(memberData);
            return customUserDetails;
        }

        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
    }
}