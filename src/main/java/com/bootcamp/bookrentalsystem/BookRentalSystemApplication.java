package com.bootcamp.bookrentalsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BookRentalSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookRentalSystemApplication.class, args);
	}

}
