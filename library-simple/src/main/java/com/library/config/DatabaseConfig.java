package com.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Checklist item 6 - Spring Boot annotations, in the simplest form.
 *
 * @Configuration = "this class tells Spring how to make things".
 * @Bean = "Spring, make this one object and keep it for the whole app".
 *
 * This bean is the connection to MySQL. Spring Boot then makes a
 * JdbcTemplate from it automatically, so the repositories never
 * open a connection themselves.
 */
@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        // DataSourceBuilder is Spring Boot's own builder.
        // It picks HikariCP (the connection pool) and the MySQL driver
        // by itself from the jdbc:mysql:// URL. Nothing else to write.
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build();
    }
}
