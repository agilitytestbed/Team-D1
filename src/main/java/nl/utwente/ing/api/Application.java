package nl.utwente.ing.api;

import nl.utwente.ing.model.persistentmodel.DatabaseConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * The Application class.
 * Used by the Spring framework to indicate where the application should be started.
 *
 * @author Daan Kooij
 */
@SpringBootApplication
public class Application {

    /**
     * Method used by the Spring framework to start the application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        DatabaseConnection.setUp("ing.db");
        SpringApplication.run(Application.class, args);
    }

    /**
     * Method used to enable Cross-Origin Resource Sharing.
     *
     * @return WebMvcConfigurerAdapter object.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
            }
        };
    }

}
