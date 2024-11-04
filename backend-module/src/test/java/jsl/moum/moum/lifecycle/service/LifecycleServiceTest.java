package jsl.moum.moum.lifecycle.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import jsl.moum.moum.lifecycle.domain.LifecycleRepository;
import jsl.moum.moum.lifecycle.domain.LifecycleTeamRepository;

class LifecycleServiceTest {

    @Spy
    @InjectMocks
    private LifecycleService lifecycleService;

    @Mock
    private LifecycleRepository lifecycleRepository;

    @Mock
    private LifecycleTeamRepository lifecycleTeamRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("test setup")
    void testSetup(){

    }

    /**
     * 모음 생성
     */

    /**
     * 모음 정보 수정
     */

    /**
     * 모음 삭제
     */

    /**
     * 모음 마감하기
     */

    /**
     * 모음 되살리기
     */

}