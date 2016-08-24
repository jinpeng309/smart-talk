package com.capslock.im.commons.packet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public abstract class AbstractSocketPacket {
    @JsonIgnore
    public abstract String getProtocolName();
}
