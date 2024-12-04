package jsl.moum.chatappmodule.health_check;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/api/chat/health-check")
    public String chatModuleCheck(){
        return "GET: /api/chat/health-check \n";
    }
}
