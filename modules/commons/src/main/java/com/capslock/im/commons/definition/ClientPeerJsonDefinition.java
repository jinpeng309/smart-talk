package com.capslock.im.commons.definition;

/**
 * Created by capslock1874.
 */
public final class ClientPeerJsonDefinition extends PeerJsonDefinition {
    /**
     * Don't let anyone instantiate this class.
     */
    private ClientPeerJsonDefinition() {
        // This constructor is intentionally empty.
    }

    public static final String CLIENT_IP = "clientIp";
    public static final String DEVICE_UUID = "devUuid";
    public static final String USER_ID = "uid";
    public static final String SERVER_IP = "serverIp";
}
