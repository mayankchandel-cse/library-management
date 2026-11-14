package com.library.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Database configuration (requirement 6): the DataSource is declared
 * explicitly as a Spring @Bean, bound to the spring.datasource.*
 * properties in application.properties. Spring Boot creates the
 * JdbcTemplate from this DataSource; the repositories @Autowire it.
 * The URL is set with setJdbcUrl (Hikari's property name) — binding
 * spring.datasource.url straight onto the pool would leave it unset.
 */
@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.datasource.driver-class-name}") String driverClassName) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }
}
