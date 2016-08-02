package com.capslock.im.commons.packet.protocol;

/**
 * Created by capslock1874.
 */
public class AuthenticationProtocol {
    public static final String NAME = "auth";

    /**
     * Don't let anyone instantiate this class.
     */
    private AuthenticationProtocol() {
        // This constructor is intentionally empty.
    }

    public static final class Inbound {
        public static final String FROM = "from";
        public static final String TOKEN = "token";
        public static final String DEVICE_TYPE = "deviceType";
        public static final String DEVICE_UUID = "deviceUuid";

        /**
         * Don't let anyone instantiate this class.
         */
        private Inbound() {
            // This constructor is intentionally empty.
        }
    }

    public static final class Outbound {
        public static final String RESULT = "r";

        /**
         * Don't let anyone instantiate this class.
         */
        private Outbound() {
            // This constructor is intentionally empty.
        }
    }
}
