package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;

import java.util.List;

@Builder
public class TempQuoteMessage extends Packet {
    private String sessionKey;
    private long target;
    private int quote;
    private long group;
    private List<MiraiTypeMessage> messageChain;

    @Override
    public String getTargetedPath() {
        return "sendTempMessage";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public TempQuoteMessage send() {
        return this.getClass().cast(super.send());
    }
}
