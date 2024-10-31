package study.moum.moum.moum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.moum.moum.moum.domain.LifecycleRepository;

@Service
@RequiredArgsConstructor
public class LifecycleService {

    private final LifecycleRepository lifecycleRepository;
}
