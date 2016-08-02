package com.capslock.im.config;

/**
 * Created by capslock1874.
 */
public final class StartServerTypeConfig {
    /**
     * Don't let anyone instantiate this class.
     */
    private StartServerTypeConfig() {
        // This constructor is intentionally empty.
    }

    public static final String SERVER_TYPE = "server.type";
    public static final String LOGIC_SERVER = "logic";
    public static final String CONNECTION_SERVER = "connection";
}
