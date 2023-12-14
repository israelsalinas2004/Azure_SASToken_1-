package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	//http://localhost:9100/api/hola
	@GetMapping("/api/hola")
	public String sayHello() {
		return "Hola Mundo!";
	}
}