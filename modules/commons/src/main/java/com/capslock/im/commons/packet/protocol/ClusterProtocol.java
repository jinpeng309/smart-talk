package com.capslock.im.commons.packet.protocol;

/**
 * Created by capslock1874.
 */
public class ClusterProtocol {
    /**
     * Don't let anyone instantiate this class.
     */
    private ClusterProtocol() {
        // This constructor is intentionally empty.
    }

    public static final String PACKET_FROM = "from";
    public static final String PACKET_TO = "to";
    public static final String PACKET_TYPE = "type";
    public static final String PACKET_DATA = "data";
    public static final String PACKET_PROTOCOL_NAME = "protocol";
    public static final String MESSAGE_FROM = "messageFrom";
    public static final String MESSAGE_TO = "messageTo";
}
