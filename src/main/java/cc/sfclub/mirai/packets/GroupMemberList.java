package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import cc.sfclub.mirai.adapts.SBSpigot;
import cc.sfclub.mirai.packets.received.sender.MiraiGroupSender;
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

    public Set<MiraiGroupSender> asList() {
        Set<MiraiGroupSender> senders = new HashSet<>();
        SBSpigot.JP.parse(getRawResponse())
                .getAsJsonArray().forEach(j -> senders.add(gson.fromJson(j, MiraiGroupSender.class)));
        return senders;
    }

    @Override
    public boolean debugPacketContent() {
        return false;
    }
}
