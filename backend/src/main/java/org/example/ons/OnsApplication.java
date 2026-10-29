package org.example.ons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnsApplication.class, args);
    }

}
