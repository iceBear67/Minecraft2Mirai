package cc.sfclub.mirai.packets;

import cc.sfclub.core.Core;
import cc.sfclub.mirai.Config;
import cc.sfclub.mirai.Packet;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Optional;

public class Auth extends Packet {
    private String authKey= Config.getInst().authKey;
    public Auth authKey(String authKey){
        this.authKey=authKey;
        return this;
    }
    @Override
    public String getTargetedPath() {
        return "auth";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }
    @Override
    public Auth send(){
        return this.getClass().cast(super.send());
    }
    public Optional<String> asSession(){
        Response response=Core.getGson().fromJson(getRawResponse(),Response.class);
        return Optional.ofNullable(response.session);
    }
    private class Response{
        private String session;
    }
}
