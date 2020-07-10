package cc.sfclub.mirai.events.mirai.group.member;

import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import lombok.Getter;

@Getter
public class MemberMuteEvent implements MemberEvent {
    private int durationSeconds;
    private MiraiSender member;
    private MiraiSender operator;
}
