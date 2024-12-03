package jsl.moum.check;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "GET: /test server is alive";
    }

//    @GetMapping("/test/chat")
//    public String test_chat(){
//        return "GET: /test/chat server is alive";
//    }

    @GetMapping("/api/chat")
    public String api_test_chat(){
        return "GET: /api/chat server is alive";
    }

    @GetMapping("/api/test")
    public String api_test(){
        return "GET: /api/test server is alive";
    }

}
