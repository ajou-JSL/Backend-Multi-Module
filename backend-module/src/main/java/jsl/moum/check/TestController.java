package jsl.moum.check;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "GET: /test server is alive";
    }

    @GetMapping("/api/health-check")
    public String backendModuleCheck(){
        return "GET: /api/health-check \n";
    }
}
