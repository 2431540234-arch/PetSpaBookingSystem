package com.petspa.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetSpaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetSpaBackendApplication.class, args);
    }
}
