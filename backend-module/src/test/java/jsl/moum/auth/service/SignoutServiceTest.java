package jsl.moum.auth.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SignoutServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private SignoutService signoutService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원탈퇴 실패 테스트 - 존재하지 않는 사용자")
    void signoutFail_MemberNotExist() {
        // given
        String username = "nonExistentUser";

        when(memberRepository.findByUsername(username)).thenReturn(null);

        // then
        CustomException thrown = assertThrows(CustomException.class, () -> {
            signoutService.signoutMember(username);
        });

        assertEquals(ErrorCode.MEMBER_NOT_EXIST, thrown.getErrorCode());
    }

    @Test
    @DisplayName("회원탈퇴 성공 테스트 - 정상 사용자")
    void signoutSuccess_ValidUser() {
        // given
        String username = "existingUser";
        MemberEntity member = new MemberEntity();
        member.setUsername(username);
        member.setActiveStatus(true);

        when(memberRepository.findByUsername(username)).thenReturn(member);

        // when
        MemberDto.Response response = signoutService.signoutMember(username);

        // then
        assertNotNull(response);
        assertEquals(username, response.getUsername());
        assertFalse(member.getActiveStatus());
        verify(memberRepository).findByUsername(username);
    }
}