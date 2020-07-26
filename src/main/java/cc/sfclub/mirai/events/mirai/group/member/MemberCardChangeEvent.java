package cc.sfclub.mirai.events.mirai.group.member;

import cc.sfclub.mirai.packets.received.sender.MiraiGroupSender;
import lombok.Getter;

@Getter
public class MemberCardChangeEvent implements MemberEvent {
    private String origin;
    private String current;
    private MiraiGroupSender member;
    private MiraiGroupSender operator;
}
