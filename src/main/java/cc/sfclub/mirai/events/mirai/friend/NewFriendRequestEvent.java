package cc.sfclub.mirai.events.mirai.friend;

import cc.sfclub.mirai.Cred;
import cc.sfclub.mirai.packets.DealFriendRequest;
import lombok.Getter;

@Getter
public class NewFriendRequestEvent extends FriendEvent {
    private long eventId;
    private long fromId;
    /**
     */
    private long groupId;
    private String nick;
    private String message;

    public void deal(int operate, String message) {
        DealFriendRequest.builder()
                .eventId(eventId)
                .fromId(fromId)
                .groupId(groupId)
                .operate(operate)
                .message(message)
                .sessionKey(Cred.sessionKey)
                .build().send();

    }
}
