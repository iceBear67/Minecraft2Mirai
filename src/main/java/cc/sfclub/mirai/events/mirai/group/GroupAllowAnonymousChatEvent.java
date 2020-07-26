package cc.sfclub.mirai.events.mirai.group;

import cc.sfclub.mirai.packets.received.sender.MiraiGroupSender;
import lombok.Getter;

@Getter
public class GroupAllowAnonymousChatEvent extends GroupEvent {
    private boolean origin;
    private boolean current;
    private MiraiGroupSender operator;
}
