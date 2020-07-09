package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Config;
import cc.sfclub.mirai.Packet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Builder;

import java.util.Optional;

@Builder
public class Release extends Packet {
    private String sessionKey;
    private long qq= Config.getInst().QQ;
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
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }
}
