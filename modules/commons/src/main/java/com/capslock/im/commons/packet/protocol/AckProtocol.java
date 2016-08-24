package com.capslock.im.commons.packet.protocol;

/**
 * Created by capslock1874.
 */
public class AckProtocol {
    /**
     * Don't let anyone instantiate this class.
     */
    private AckProtocol() {
        // This constructor is intentionally empty.
    }

    public static final String NAME = "ack";

    public static final class Outbound {
        public static final String UUID = "uuid";
        public static final String TO = "to";
    }
}
