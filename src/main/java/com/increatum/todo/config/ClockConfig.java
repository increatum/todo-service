package com.increatum.todo.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class ClockConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
