package org.example.haruapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HaruApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaruApiApplication.class, args);
    }

}