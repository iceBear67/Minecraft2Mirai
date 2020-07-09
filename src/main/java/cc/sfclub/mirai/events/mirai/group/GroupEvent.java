package cc.sfclub.mirai.events.mirai.group;

import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import cc.sfclub.mirai.packets.received.sender.MiraiGroup;
import lombok.Getter;

@Getter
public abstract class GroupEvent extends MiraiMessage {
    private MiraiGroup group;
}
