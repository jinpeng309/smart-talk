package com.capslock.im.commons.packet.protocol.rpc;

/**
 * Created by capslock1874.
 */
public class StorePrivateChatMessageRpcProtocol {
    /**
     * Don't let anyone instantiate this class.
     */
    private StorePrivateChatMessageRpcProtocol() {
        // This constructor is intentionally empty.
    }

    public static final class Request {
        /**
         * Don't let anyone instantiate this class.
         */
        private Request() {
            // This constructor is intentionally empty.
        }

        public static final String DATA = "data";
        public static final String OWNER = "owner";
    }

    public static final class Response {
        /**
         * Don't let anyone instantiate this class.
         */
        private Response() {
            // This constructor is intentionally empty.
        }

        public static final String OWNER = "owner";
    }
}
