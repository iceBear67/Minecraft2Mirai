package cc.sfclub.mirai.events.mirai.group;

import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import lombok.Getter;

@Getter
public class MemberMuteEvent extends GroupEvent {
    private int durationSeconds;
    private MiraiSender member;
    private MiraiSender operator;
}
