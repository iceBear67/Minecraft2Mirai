package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Builder;

import java.util.Optional;

@Builder
public class Release extends Packet {
    private String sessionKey;
    private long qq;
    public Optional<String> asMessage(){
        JsonObject json=JsonParser.parseString(getRawResponse()).getAsJsonObject();
        if(!json.has("msg"))return Optional.empty();
        return Optional.ofNullable(json.get("msg").getAsString());
    }

    @Override
    public String getTargetedPath() {
        return "release";
    }

    @Override
    public Release send() {
        return this.getClass().cast(super.sendSync());
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }
}
