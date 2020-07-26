package cc.sfclub.mirai.events.mirai.group.member;

import cc.sfclub.mirai.packets.received.sender.MiraiGroupSender;
import lombok.Getter;

@Getter
public class MemberMuteEvent implements MemberEvent {
    private int durationSeconds;
    private MiraiGroupSender member;
    private MiraiGroupSender operator;
}
