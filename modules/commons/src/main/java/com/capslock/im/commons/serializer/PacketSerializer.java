package com.capslock.im.commons.serializer;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.packet.ProtocolPacket;
import com.capslock.im.commons.packet.outbound.AbstractSocketOutboundPacket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableMap;
import org.reflections.Reflections;

/**
 * Created by capslock1874.
 */
public final class PacketSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    private static final ImmutableMap<Class<? extends AbstractSocketOutboundPacket>, String> protocolMap;

    static {
        final ImmutableMap.Builder<Class<? extends AbstractSocketOutboundPacket>, String> builder =
                ImmutableMap.builder();
        final Reflections reflections = new Reflections("com.capslock.im.commons.packet.outbound");
        reflections
                .getSubTypesOf(AbstractSocketOutboundPacket.class)
                .forEach(clazz -> builder.put(clazz, clazz.getAnnotation(Protocol.class).value()));
        protocolMap = builder.build();
    }

    private PacketSerializer() {
        //no instance
    }

    public static <T extends AbstractSocketOutboundPacket> ProtocolPacket serialize(final T packet) throws JsonProcessingException {
        final String protocolName = protocolMap.get(packet.getClass());
        return new ProtocolPacket(protocolName, objectMapper.valueToTree(packet));
    }
}
