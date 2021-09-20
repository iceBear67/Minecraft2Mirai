package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import cc.sfclub.mirai.packets.received.sender.MiraiGroup;
import cc.sfclub.mirai.utils.AdaptedJsonParser;
import com.google.gson.JsonParser;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class GroupList extends Packet {
    private String sessionKey;

    @Override
    public String getTargetedPath() {
        return "groupList?sessionKey=" + sessionKey;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public GroupList send() {
        super.sendSync();
        return this;
    }

    public List<MiraiGroup> asGroups() {
        List<MiraiGroup> groups = new ArrayList<>();
        AdaptedJsonParser.parseString(getRawResponse())
                .getAsJsonArray()
                .forEach(j -> {
                    groups.add(gson.fromJson(j, MiraiGroup.class));
                });
        return groups;
    }
}
