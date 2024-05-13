package com.increatum.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SpringDocConfiguration {

    @Bean(name = "com.increatum.todo.config.SpringDocConfiguration.apiInfo")
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("A TODO-Todo list application")
                                .description("A simple application to handle todos.")
                                .license(
                                        new License()
                                                .name("Eclipse Public License 2.0")
                                                .url("https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html")
                                )
                                .version("1.0.0")
                )
        ;
    }
}