package study.moum.moum.moum.service;

import jsl.moum.moum.moum.service.LifecycleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import jsl.moum.moum.moum.domain.LifecycleRepository;
import jsl.moum.moum.moum.domain.LifecycleTeamRepository;

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

}