package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.example.contratos.IAzureDataToolService;
import com.example.controllers.AzureController;
import com.example.services.AzureDataToolsService;


@SpringBootApplication
//@ComponentScan(basePackages = "com.example")
@ComponentScan(basePackageClasses = AzureController.class) //sin esta instrucción no reconoce AzureController
@ComponentScan(basePackageClasses = AzureDataToolsService.class) //sin esta instrucción no reconoce AzureController
@ComponentScan(basePackageClasses = IAzureDataToolService.class) //sin esta instrucción no reconoce AzureController
public class SpringbootAzureSasToken1Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootAzureSasToken1Application.class, args);
	}
}
