package jsl.moum.moum.moum.service;

import jsl.moum.moum.moum.domain.LifecycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LifecycleService {

    private final LifecycleRepository lifecycleRepository;
}
