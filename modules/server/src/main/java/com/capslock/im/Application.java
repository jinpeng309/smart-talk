package com.capslock.im;

import com.capslock.im.component.SessionManager;
import com.capslock.im.config.StartServerTypeConfig;
import com.capslock.im.net.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by capslock1874.
 */
@SpringBootApplication
public class Application implements CommandLineRunner {
    @Value("${server.type}")
    private String startType;
    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(final String... args) throws Exception {
        if (StartServerTypeConfig.CONNECTION_SERVER.equals(startType)) {
            context.getBean(Connector.class).start();
        } else if (StartServerTypeConfig.LOGIC_SERVER.equals(startType)) {
            context.getBean(SessionManager.class).start();
        }
    }
}
