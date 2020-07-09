package cc.sfclub.mirai.events.mirai.friend;

import lombok.Getter;

@Getter
public class FriendRecallEvent extends FriendEvent {
    private long authorId;
    private int messageId;
    private long operator;
    private int time;
}
