package cc.sfclub.mirai.packets;

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
        logger.info(getRawResponse());
        return this;
    }
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
