package cc.sfclub.mirai.events.mirai.group.member;

import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import lombok.Getter;

@Getter
public class MemberJoinEvent implements MemberEvent {
    private MiraiSender member;
}
