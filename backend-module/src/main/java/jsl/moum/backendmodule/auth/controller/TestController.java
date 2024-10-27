package jsl.moum.backendmodule.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jsl.moum.backendmodule.auth.domain.CustomUserDetails;

@RestController
public class TestController {

    @GetMapping("/test")
    public String testPage(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        // System.out.println(customUserDetails.getUsername());

//        return new ResponseEntity<>(HttpStatus.OK);
        return "test";
    }
}
