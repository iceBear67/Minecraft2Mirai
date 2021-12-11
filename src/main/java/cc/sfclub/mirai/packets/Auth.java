package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import lombok.Builder;

import java.util.Optional;

@Builder
public class Auth extends Packet {
    private String verifyKey;
    @Override
    public String getTargetedPath() {
        return "verify";
    }

    @Override
    public boolean debugPacketContent() {
        return false;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }
    @Override
    public Auth send(){
        return this.getClass().cast(super.sendSync());
    }
    public Optional<String> asSession(){
        System.out.println(getRawResponse());
        Response response = gson.fromJson(getRawResponse(), Response.class);
        return Optional.ofNullable(response.session);
    }
    private class Response{
        private String session;
    }
}
