package jsl.moum.backendmodule.moum.moum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import jsl.moum.backendmodule.moum.moum.service.MoumService;

@RestController
@RequiredArgsConstructor
public class MoumController {

    private final MoumService moumService;
}
