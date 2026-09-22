package com.civicshield.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CivicShieldSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CivicShieldSimulatorApplication.class, args);
    }
}
