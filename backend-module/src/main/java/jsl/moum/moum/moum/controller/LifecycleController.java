package jsl.moum.moum.moum.controller;

import jsl.moum.moum.moum.service.LifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class LifecycleController {

    private final LifecycleService lifecycleService;
}
