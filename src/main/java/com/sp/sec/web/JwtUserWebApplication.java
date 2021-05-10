package com.sp.sec.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JwtUserWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtUserWebApplication.class, args);
	}

}