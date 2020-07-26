package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;

import java.util.List;

@Builder
public class FriendMessage extends Packet {
    private String sessionKey;
    private long target;
    private List<MiraiTypeMessage> messageChain;

    @Override
    public String getTargetedPath() {
        return "sendFriendMessage";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public FriendMessage send() {
        return this.getClass().cast(super.send());
    }

}
