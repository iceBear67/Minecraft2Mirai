package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;

import java.util.List;

@Builder
public class FriendQuoteMessage extends Packet {
    private String sessionKey;
    private int quote;
    private long target;
    private List<MiraiTypeMessage> messageChain;

    @Override
    public String getTargetedPath() {
        return "sendFriendMessage";
    }

    @Override
    public Packet.HttpMethod getMethod() {
        return Packet.HttpMethod.POST;
    }

    @Override
    public FriendQuoteMessage send() {
        return this.getClass().cast(super.send());
    }

}
