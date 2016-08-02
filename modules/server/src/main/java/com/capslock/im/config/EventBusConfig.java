package com.capslock.im.config;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by capslock1874.
 */
@Configuration
public class EventBusConfig {
    @Bean(name = "logicServerClusterEventBus")
    public EventBus logicServerClusterEventBus() {
        return new EventBus("logicServerClusterEventBus");
    }

    @Bean(name = "connServerClusterEventBus")
    public EventBus connServerClusterEventBus() {
        return new EventBus("connServerClusterEventBus");
    }
}
