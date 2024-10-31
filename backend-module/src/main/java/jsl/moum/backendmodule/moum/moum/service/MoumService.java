package jsl.moum.backendmodule.moum.moum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jsl.moum.backendmodule.moum.moum.domain.MoumRepository;

@Service
@RequiredArgsConstructor
public class MoumService {

    private final MoumRepository moumRepository;
}
