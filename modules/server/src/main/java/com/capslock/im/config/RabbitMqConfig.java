package com.capslock.im.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by capslock1874.
 */
@Configuration
public class RabbitMqConfig {
    @Bean
    public Connection getConnection() throws IOException, TimeoutException {
        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("139.162.57.135");
        return factory.newConnection();
    }
}
