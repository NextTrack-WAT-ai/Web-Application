package com.nexttrack.spring_boot_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nexttrack.spring_boot_app.repository.UserRepo;

@SpringBootApplication
public class SpringBootAppApplication {
	@Autowired
	UserRepo userRepo; 

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAppApplication.class, args);
	}
 
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")  
						.allowedOrigins("http://localhost:5173")  // DO NOT MODIFY
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}
}
