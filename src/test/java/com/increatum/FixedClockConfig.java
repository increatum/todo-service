package com.increatum;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class FixedClockConfig {

    @Bean
    Clock clock() {
        return Clock.fixed(Instant.parse("2024-05-12T23:00:00.00Z"), ZoneId.of("UTC"));
    }

}
