package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import lombok.Builder;

@Builder
public class Bind extends Packet {
    private String sessionKey;
    private long qq;
    public Bind sessionKey(String key){
        this.sessionKey=key;
        return this;
    }

    public Bind qq(long qq) {
        this.qq = qq;
        return this;
    }

    @Override
    public String getTargetedPath() {
        return "bind";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }
}
