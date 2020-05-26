package com.robertoman.sproxy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static com.robertoman.sproxy.util.ANSIColors.*;

@AutoConfigureOrder
@Configuration
@Slf4j
public class BannerConfig {

    @PostConstruct
    private void print() {
        printBanner();
    }

    private static void printBanner() {
        String banner =
                ANSI_GREEN + "\n+----+  +-----+  +-----+  +-----+  *   *  \\   /" + ANSI_RESET +
                ANSI_GREEN + "\n||      ||   ||  ||   ||  ||   ||   \\ /    \\ /" + ANSI_RESET +
                ANSI_WHITE + "\n+----+  ||----+  ||----+  ||   ||    *      !" + ANSI_RESET +
                ANSI_RED + "\n    ||  ||       || \\\\    ||   ||   / \\     |" + ANSI_RESET +
                ANSI_RED + "\n+----+  ||       ||  \\\\   +-----+  *   *    !" + ANSI_RESET;

        log.info(banner);
    }
}
