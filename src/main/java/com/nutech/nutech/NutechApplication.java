package com.nutech.nutech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses=NutechApplication.class)
public class NutechApplication {

	public static void main(String[] args) {
		SpringApplication.run(NutechApplication.class, args);
	}

}
