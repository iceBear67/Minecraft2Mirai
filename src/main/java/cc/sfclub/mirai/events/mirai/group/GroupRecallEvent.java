package cc.sfclub.mirai.events.mirai.group;

import cc.sfclub.mirai.packets.received.sender.MiraiGroupSender;
import lombok.Getter;

@Getter
public class GroupRecallEvent extends GroupEvent {
    private long authorId;
    private int messageId;
    private int time;
    private MiraiGroupSender operator;

}
