package com.library.main;

import com.library.gui.MainFrame;
import com.library.service.LibraryService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.swing.SwingUtilities;

/**
 * LibraryApplication - entry point of the Spring Boot + JDBC
 * Library Management System.
 *
 * <p>Spring Boot starts with no web server (this is a desktop Swing
 * application, not a website). The Spring context creates the
 * LibraryService bean, and the GUI receives that bean - the GUI never
 * does {@code new LibraryService()} itself, so all database work goes
 * through Spring.</p>
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.library")
public class LibraryApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(LibraryApplication.class)
                .web(WebApplicationType.NONE)
                .headless(false)
                .run(args);
        LibraryService service = context.getBean(LibraryService.class);
        SwingUtilities.invokeLater(() -> new MainFrame(service).setVisible(true));
    }
}
