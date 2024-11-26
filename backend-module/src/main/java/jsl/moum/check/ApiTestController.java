package jsl.moum.check;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiTestController {

    @GetMapping("/apiv2/test")
    public String getApiTest(){
        return "api test";
    }
}
