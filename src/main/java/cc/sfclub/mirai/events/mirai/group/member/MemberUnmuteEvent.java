package cc.sfclub.mirai.events.mirai.group.member;

import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import lombok.Getter;

@Getter
public class MemberUnmuteEvent implements MemberEvent {
    private MiraiSender member;
    private MiraiSender operator;
}
