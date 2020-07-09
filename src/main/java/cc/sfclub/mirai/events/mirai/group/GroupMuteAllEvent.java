package cc.sfclub.mirai.events.mirai.group;

import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import lombok.Getter;

@Getter
public class GroupMuteAllEvent extends GroupEvent {
    private boolean origin;
    private boolean current;
    private MiraiSender operator;
}
