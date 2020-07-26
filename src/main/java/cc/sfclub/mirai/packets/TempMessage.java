package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;

import java.util.List;

@Builder
public class TempMessage extends Packet {
    private String sessionKey;
    private long target;
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
    public TempMessage send() {
        return this.getClass().cast(super.send());
    }

}
