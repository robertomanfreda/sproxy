package com.robertoman.sproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;
import java.util.Properties;

import static com.robertoman.sproxy.utils.ANSIColors.*;

@EnableConfigurationProperties
@SpringBootApplication
public class Sproxy {

    public static void main(String[] args) throws IOException {
        System.out.println(ANSI_GREEN + "+----  +---+  +---+  +---+  *   *  \\   /" + ANSI_RESET);
        System.out.println(ANSI_GREEN +"|      |   |  |   |  |   |   \\ /    \\ /" + ANSI_RESET);
        System.out.println(ANSI_WHITE + "+---+  |---+  |---+  |   |    *      |" + ANSI_RESET);
        System.out.println(ANSI_RED +"    |  |      | \\    |   |   / \\     |" + ANSI_RESET);
        System.out.println(ANSI_RED + "----+  |      |  \\   +---+  *   *    |" + ANSI_RESET);
        SpringApplication application = new SpringApplication(Sproxy.class);

        Properties properties = com.robertoman.sproxy.utils.Properties.loadProperties();
        /*https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml?&page=99*/
        properties.setProperty("server.port", "6380");
        properties.setProperty("spring.application.name", "sproxy");
        properties.setProperty("spring.main.banner-mode", "off");
        properties.setProperty("logging.level.org.springframework", "info");
        application.setDefaultProperties(properties);

        application.run(args);
    }
}
