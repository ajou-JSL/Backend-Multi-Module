package jsl.moum.backendmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "jsl.moum.backendmodule")
public class BackendModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendModuleApplication.class, args);
    }

}
