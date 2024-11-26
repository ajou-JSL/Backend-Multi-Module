package jsl.moum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class MoumApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(MoumApplication.class, args);
//    }

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application.yml,"
            + "classpath:application-release.yml";

    public static void main(String[] args) {

        new SpringApplicationBuilder(MoumApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);;
    }

}
