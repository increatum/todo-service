package com.increatum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

import com.increatum.todo.TodoServiceApplication;

@TestConfiguration(proxyBeanMethods = false)
public class TestTodoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(TodoServiceApplication::main).with(TestTodoServiceApplication.class).run(args);
	}

}
