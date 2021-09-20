package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import cc.sfclub.mirai.packets.received.contact.MiraiContact;
import cc.sfclub.mirai.utils.AdaptedJsonParser;
import com.google.gson.JsonParser;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class FriendList extends Packet {
    private final String sessionKey;

    @Override
    public String getTargetedPath() {
        return "friendList?sessionKey=" + sessionKey;
    }

    @Override
    public FriendList send() {
        return (FriendList) super.sendSync();
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    public List<MiraiContact> asGroups() {
        List<MiraiContact> contacts = new ArrayList<>();
        AdaptedJsonParser.parseString(getRawResponse())
                .getAsJsonArray()
                .forEach(j -> contacts.add(gson.fromJson(j, MiraiContact.class)));
        return contacts;
    }
}
