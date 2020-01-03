package com.robertomanfreda.sproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SProxy {

    public static void main(String[] args) {
        SpringApplication.run(SProxy.class, args);
    }

}
