package cc.sfclub.mirai.events.mirai.friend;

import cc.sfclub.mirai.Config;
import cc.sfclub.mirai.packets.DealFriendRequest;
import lombok.Getter;

@Getter
public class NewFriendRequestEvent extends FriendEvent {
    private long eventId;
    private long fromId;
    /**
     * 申请人如果通过某个群添加好友，该项为该群群号；否则为0
     */
    private long groupId;
    private String nick;
    private String message;

    public DealFriendRequest deal(int operate, String message) {
        return DealFriendRequest.builder()
                .eventId(eventId)
                .fromId(fromId)
                .groupId(groupId)
                .operate(operate)
                .message(message)
                .sessionKey(Config.getInst().authKey)
                .build();

    }
}
