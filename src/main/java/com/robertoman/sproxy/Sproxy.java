package com.robertoman.sproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Properties;

import static com.robertoman.sproxy.util.ANSIColors.*;

@EnableConfigurationProperties
@Slf4j
@SpringBootApplication
public class Sproxy {

    public static void main(String[] args) {
        SpringApplication sproxyApp = new SpringApplication(Sproxy.class);

        Properties properties = new Properties();
        /*https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml?&page=99*/
        properties.setProperty("server.port", "6380");
        properties.setProperty("spring.main.banner-mode", "off");
        properties.setProperty("spring.application.name", "sproxy");
        properties.setProperty("spring.output.ansi.enabled", "always");
        properties.setProperty("logging.level.org.springframework", "info");
        properties.setProperty("logging.file.name", "/var/log/sproxy/sproxy");

        sproxyApp.setDefaultProperties(properties);

        sproxyApp.run(args);
        printBanner();
    }

    private static void printBanner() {
        String banner = ANSI_GREEN + "\n+----  +---+  +---+  +---+  *   *  \\   /" + ANSI_RESET +
                ANSI_GREEN + "\n|      |   |  |   |  |   |   \\ /    \\ /" + ANSI_RESET +
                ANSI_WHITE + "\n+---+  |---+  |---+  |   |    *      |" + ANSI_RESET +
                ANSI_RED + "\n    |  |      | \\    |   |   / \\     |" + ANSI_RESET +
                ANSI_RED + "\n----+  |      |  \\   +---+  *   *    |" + ANSI_RESET;

        log.info(banner);
    }
}
