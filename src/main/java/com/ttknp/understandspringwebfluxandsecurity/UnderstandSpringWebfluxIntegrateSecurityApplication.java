package com.ttknp.understandspringwebfluxandsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.ttknp")
@SpringBootApplication
public class UnderstandSpringWebfluxIntegrateSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnderstandSpringWebfluxIntegrateSecurityApplication.class, args);
	}

}
