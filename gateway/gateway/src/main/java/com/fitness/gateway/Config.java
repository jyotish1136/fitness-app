package com.fitness.gateway;

import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class Config {
    @Bean
    public ApplicationRunner runner(Environment env) {
        return args -> {
            System.out.println("Route 0 id = " +
                    env.getProperty("spring.cloud.gateway.routes[0].uri"));
        };
    }
    @Bean
    public GlobalFilter debugPath() {
        return (exchange, chain) -> {
            System.out.println("Incoming Path: " +
                    exchange.getRequest().getURI().getPath());
            return chain.filter(exchange);
        };
    }
}
