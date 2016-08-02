package com.capslock.im.component;

import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class ClientInfo {
    private final long uid;
    private final String deviceUuid;
    private final String connServerNodeIp;

}
