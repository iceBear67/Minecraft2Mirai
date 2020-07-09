package cc.sfclub.mirai.events.mirai.group;

import cc.sfclub.mirai.Config;
import cc.sfclub.mirai.packets.DealGroupJoinRequest;
import lombok.Getter;

@Getter
public class MemberJoinRequestEvent extends GroupEvent {
    private long eventId;
    private long fromId;
    private long groupId;
    private String groupName;
    private String nick;
    private String message;

    public DealGroupJoinRequest deal(int operate, String message) {
        return DealGroupJoinRequest.builder()
                .eventId(eventId)
                .fromId(fromId)
                .groupId(groupId)
                .operate(operate)
                .message(message)
                .sessionKey(Config.getInst().authKey)
                .build();

    }
}
