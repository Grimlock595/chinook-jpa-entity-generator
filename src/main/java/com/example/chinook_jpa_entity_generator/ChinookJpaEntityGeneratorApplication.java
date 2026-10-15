package com.example.chinook_jpa_entity_generator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChinookJpaEntityGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChinookJpaEntityGeneratorApplication.class, args);
    }

    @Bean
    CommandLineRunner generateJpaEntities(JpaEntityGenerator generator) {
        return args -> generator.generateEntities();
    }
}
