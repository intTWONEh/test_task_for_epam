package com.test.todolist;

import com.test.todolist.services.MenuApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TodolistApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext Context = SpringApplication.run(TodolistApplication.class, args);
		Context.getBean(MenuApp.class).startApp();
		Context.close();
	}
}
