package com.IDS.ja3ids;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Ja3idsApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ja3idsApplication.class, args);
    }
}
