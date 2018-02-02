package api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Application class.
 * Used by the Spring framework to indicate where the application should be started.
 * @author Daan Kooij
 */
@SpringBootApplication
public class Application {

    /**
     * Method used by the Spring framework to start the application.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
