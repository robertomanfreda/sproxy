package com.robertoman.sproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Properties;

import static com.robertoman.sproxy.util.ANSIColors.*;

@EnableConfigurationProperties
@SpringBootApplication
public class Sproxy {

    public static void main(String[] args) {
        printBanner();

        SpringApplication sproxyApp = new SpringApplication(Sproxy.class);

        Properties properties = new Properties();
        /*https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml?&page=99*/
        properties.setProperty("server.port", "6380");
        properties.setProperty("spring.application.name", "sproxy");
        properties.setProperty("spring.main.banner-mode", "off");
        properties.setProperty("logging.level.org.springframework", "info");
        sproxyApp.setDefaultProperties(properties);

        sproxyApp.run(args);
    }

    private static void printBanner() {
        String banner = ANSI_GREEN + "+----  +---+  +---+  +---+  *   *  \\   /" + ANSI_RESET +
                ANSI_GREEN + "\n|      |   |  |   |  |   |   \\ /    \\ /" + ANSI_RESET +
                ANSI_WHITE + "\n+---+  |---+  |---+  |   |    *      |" + ANSI_RESET +
                ANSI_RED + "\n    |  |      | \\    |   |   / \\     |" + ANSI_RESET +
                ANSI_RED + "\n----+  |      |  \\   +---+  *   *    |" + ANSI_RESET;

        System.out.println(banner);
    }
}
