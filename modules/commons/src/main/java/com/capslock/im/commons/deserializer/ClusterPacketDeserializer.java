package com.capslock.im.commons.deserializer;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.ProtocolPacket;
import com.capslock.im.commons.packet.cluster.*;
import com.capslock.im.commons.packet.protocol.ClusterProtocol;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.IOException;

/**
 * Created by capslock1874.
 */
public final class ClusterPacketDeserializer {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());

    /**
     * Don't let anyone instantiate this class.
     */
    private ClusterPacketDeserializer() {
        // This constructor is intentionally empty.
    }

    public static Packet deserialize(final String rawData) throws IOException {
        final JsonNode jsonNode = objectMapper.readTree(rawData);
        final String type = jsonNode.get(ClusterProtocol.PACKET_TYPE).asText();
        final PacketType packetType = PacketType.valueOf(type);
        Packet packet;
        switch (packetType) {
            case C2S:
                packet = deserializeClientToSessionPacket(jsonNode);
                break;
            case S2C:
                packet = deserializeSessionToClientPacket(jsonNode);
                break;
            case S2S:
                packet = deserializeSessionToSessionPacket(jsonNode);
                break;
            default:
                throw new IllegalArgumentException("unknown packet type " + type);
        }
        return packet;
    }

    private static Packet deserializeClientToSessionPacket(final JsonNode jsonNode) throws IOException {
        final Packet packet;
        final ClientPeer clientPeer =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_FROM).toString(), ClientPeer.class);
        final LogicServerPeer logicServerPeer =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_TO).toString(), LogicServerPeer.class);
        final ProtocolPacket protocolPacket =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_DATA).toString(), ProtocolPacket.class);
        packet = new ClientToSessionPacket(clientPeer, logicServerPeer, protocolPacket);
        return packet;
    }

    private static Packet deserializeSessionToSessionPacket(final JsonNode jsonNode) throws IOException {
        final Packet packet;
        final LogicServerPeer serverFrom =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_FROM).toString(), LogicServerPeer.class);
        final LogicServerPeer serverTo =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_TO).toString(), LogicServerPeer.class);
        final ClientPeer clientFrom =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.MESSAGE_FROM).toString(), ClientPeer.class);
        final ClientPeer clientTo =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.MESSAGE_TO).toString(), ClientPeer.class);
        final ProtocolPacket protocolPacket =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_DATA).toString(), ProtocolPacket.class);
        packet = new SessionToSessionPacket(serverFrom, serverTo, protocolPacket, clientFrom, clientTo);
        return packet;
    }

    private static Packet deserializeSessionToClientPacket(final JsonNode jsonNode) throws IOException {
        final Packet packet;
        final LogicServerPeer fromServer =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_FROM).toString(), LogicServerPeer.class);
        final ClientPeer toClient =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_TO).toString(), ClientPeer.class);
        final ProtocolPacket protocolPacket =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_DATA).toString(), ProtocolPacket.class);
        packet = new SessionToClientPacket(fromServer, toClient, protocolPacket);
        return packet;
    }
}
