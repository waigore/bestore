package com.example.estore;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"com.example.estore.entity"})
@EnableJpaRepositories(basePackages = "com.example.estore.repository")
public class ApplicationConfig {
}
