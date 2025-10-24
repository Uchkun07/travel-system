package org.example.springproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan("org.example.springproject.mapper")
public class SpringProjectApplication {
	public static void main(String[] args) {
		System.out.println("当前工作目录: " + System.getProperty("user.dir"));
		SpringApplication.run(SpringProjectApplication.class, args);
	}
}
