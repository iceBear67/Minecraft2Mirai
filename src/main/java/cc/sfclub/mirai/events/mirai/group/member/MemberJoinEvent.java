package cc.sfclub.mirai.events.mirai.group.member;

import cc.sfclub.mirai.packets.received.sender.MiraiGroupSender;
import lombok.Getter;

@Getter
public class MemberJoinEvent implements MemberEvent {
    private MiraiGroupSender member;
}
