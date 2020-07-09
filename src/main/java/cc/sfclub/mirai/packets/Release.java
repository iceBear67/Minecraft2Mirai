package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Config;
import cc.sfclub.mirai.Packet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Optional;

public class Release extends Packet {
    private String sessionKey;
    private long qq= Config.getInst().QQ;
    public Release sessionKey(String key){
        this.sessionKey=key;
        return this;
    }
    public Release qq(long qq){
        this.qq=qq;
        return this;
    }
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
