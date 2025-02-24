package com.dahye.speakerplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpeakerPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeakerPlatformApplication.class, args);
    }

}
