package com.dahye.speakerplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpeakerPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeakerPlatformApplication.class, args);
    }

}
