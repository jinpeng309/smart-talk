package com.capslock.im.processor.processor.rpc;

import com.capslock.im.commons.packet.outbound.SocketAckResponse;
import com.capslock.im.component.session.Session;
import com.capslock.im.event.ClusterPacketOutboundEvent.ClusterPacketOutboundEvent;
import com.capslock.im.event.ClusterPacketOutboundEvent.SessionToClientPacketRequest;
import com.capslock.im.event.Event;
import com.capslock.im.event.rpcEvent.RpcEvent;
import com.capslock.im.event.rpcEvent.RpcEventType;
import com.capslock.im.event.rpcEvent.StorePrivateChatMessageSuccessEvent;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
public class StorePrivateChatMessageSucceedProcessor extends AbstractRpcProcessor {
    @Override
    public void process(final RpcEvent event, final Session session, final ArrayList<Event> output) {
        final StorePrivateChatMessageSuccessEvent successEvent = (StorePrivateChatMessageSuccessEvent) event;
        final SocketAckResponse ackResponse = new SocketAckResponse(successEvent.getUuid(),
                successEvent.getOwnerUid());
        final SessionToClientPacketRequest s2cPacket = new SessionToClientPacketRequest(
                ackResponse, successEvent.getOwner());
        output.add(new ClusterPacketOutboundEvent(s2cPacket));
    }

    @Override
    public RpcEventType getType() {
        return RpcEventType.STORE_PRIVATE_CHAT_MESSAGE_SUCCEED;
    }
}
