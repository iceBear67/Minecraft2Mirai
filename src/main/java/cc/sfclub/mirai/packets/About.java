package cc.sfclub.mirai.packets;

import cc.sfclub.core.Core;
import cc.sfclub.mirai.Packet;
import lombok.Builder;

@Builder
public class About extends Packet {
    @Override
    public String getTargetedPath() {
        return "about";
    }
    @Override
    public About send(){
        Result result=super.send().asResult();
        Core.getLogger().info(getRawResponse());
        return this;
    }
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
