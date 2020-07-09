package cc.sfclub.mirai.events.mirai.group;

import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import lombok.Getter;

@Getter
public class MemberUnmuteEvent extends GroupEvent {
    private MiraiSender member;
    private MiraiSender operator;
}
