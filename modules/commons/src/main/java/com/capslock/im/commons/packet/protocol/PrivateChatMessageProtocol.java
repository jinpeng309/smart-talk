package com.capslock.im.commons.packet.protocol;

/**
 * Created by capslock1874.
 */
public final class PrivateChatMessageProtocol {
    /**
     * Don't let anyone instantiate this class.
     */
    private PrivateChatMessageProtocol() {
        // This constructor is intentionally empty.
    }
    public static final String NAME = "message";
    public static final String FROM = "from";
    public static final String TO = "to";
}
