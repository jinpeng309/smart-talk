package com.capslock.im.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by capslock1874.
 */
public class StorageServerCondition implements Condition {
    @Override
    public boolean matches(final ConditionContext conditionContext, final AnnotatedTypeMetadata annotatedTypeMetadata) {
        final String serverType = conditionContext.getEnvironment().getProperty(StartServerTypeConfig.SERVER_TYPE);
        return StartServerTypeConfig.STORAGE_SERVER.equals(serverType);
    }
}
