package cc.sfclub.mirai.events.mirai.group.bot;

import cc.sfclub.mirai.Cred;
import cc.sfclub.mirai.packets.DealGroupInviteRequest;
import lombok.Getter;

@Getter
public class BotInvitedJoinGroupRequestEvent implements BotEvent {
    private long eventId;
    private long fromId;
    private long groupId;
    private String groupName;
    private String nick;
    private String message;

    public void deal(int operate, String message) {
        DealGroupInviteRequest.builder()
                .eventId(eventId)
                .fromId(fromId)
                .groupId(groupId)
                .operate(operate)
                .message(message)
                .sessionKey(Cred.sessionKey)
                .build()
                .send();

    }
}
