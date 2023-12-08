package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	//http://localhost:9100/api/hola
	@RequestMapping(method = RequestMethod.GET, value = "/api/hola")
	public String sayHello() {
		return "Hola Mundo!";
	}
}