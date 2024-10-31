package study.moum.moum.moum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import study.moum.moum.moum.service.LifecycleService;


@RestController
@RequiredArgsConstructor
public class LifecycleController {

    private final LifecycleService lifecycleService;
}
