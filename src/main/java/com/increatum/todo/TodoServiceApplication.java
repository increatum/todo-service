package com.increatum.todo;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

import com.fasterxml.jackson.databind.Module;

@SpringBootApplication(
	    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
	)
@ComponentScan(
	    basePackages = {"com.increatum.todo.api" , "com.increatum.todo.config", "com.increatum.todo.controller", "com.increatum.todo.db"},
	    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
	)
public class TodoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoServiceApplication.class, args);
	}

    @Bean(name = "com.increatum.todo.TodoServiceApplication.jsonNullableModule")
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

}
