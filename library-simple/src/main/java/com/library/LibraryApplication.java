package com.library;

import com.library.service.LibraryService;
import com.library.ui.ConsoleMenu;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Entry point. Spring Boot starts with no web server (console app).
 * Spring creates LibraryService, and we hand that bean to the menu.
 * The menu never does "new LibraryService()" itself.
 */
@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(LibraryApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
        LibraryService service = context.getBean(LibraryService.class);
        new ConsoleMenu(service).run();
        context.close();
    }
}
