package jsl.moum.backendmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BackendModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendModuleApplication.class, args);
	}

}
