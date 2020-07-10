package cc.sfclub.mirai.packets;

import cc.sfclub.core.Core;
import cc.sfclub.mirai.Packet;
import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import com.google.gson.JsonParser;
import lombok.Builder;

import java.util.HashSet;
import java.util.Set;

@Builder
public class GroupMemberList extends Packet {
    private String sessionKey;
    private long target;

    @Override
    public String getTargetedPath() {
        return "memberList?sessionKey=" + sessionKey + "&target=" + target;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public GroupMemberList send() {
        super.send();
        return this;
    }

    public Set<MiraiSender> asList() {
        Set<MiraiSender> senders = new HashSet<>();
        JsonParser.parseString(getRawResponse())
                .getAsJsonArray().forEach(j -> senders.add(Core.getGson().fromJson(j, MiraiSender.class)));
        return senders;
    }
}
