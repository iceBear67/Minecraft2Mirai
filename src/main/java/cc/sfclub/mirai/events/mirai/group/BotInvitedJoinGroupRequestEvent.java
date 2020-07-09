package cc.sfclub.mirai.events.mirai.group;

import cc.sfclub.mirai.Config;
import cc.sfclub.mirai.packets.DealGroupInviteRequest;
import lombok.Getter;

@Getter
public class BotInvitedJoinGroupRequestEvent extends GroupEvent {
    private long eventId;
    private long fromId;
    private long groupId;
    private String groupName;
    private String nick;
    private String message;

    public DealGroupInviteRequest deal(int operate, String message) {
        return DealGroupInviteRequest.builder()
                .eventId(eventId)
                .fromId(fromId)
                .groupId(groupId)
                .operate(operate)
                .message(message)
                .sessionKey(Config.getInst().authKey)
                .build();

    }
}