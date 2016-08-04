package com.capslock.im.storage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by capslock1874.
 */
@SpringBootApplication
public class Application implements CommandLineRunner {
    @Override
    public void run(final String... args) throws Exception {

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
