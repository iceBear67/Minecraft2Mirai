package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import lombok.Builder;

@Builder
public class Verify extends Packet {
    private String sessionKey;
    private long qq;
    public Verify sessionKey(String key){
        this.sessionKey=key;
        return this;
    }

    public Verify qq(long qq) {
        this.qq = qq;
        return this;
    }

    @Override
    public String getTargetedPath() {
        return "verify";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }
}
