package cc.sfclub.mirai.events.mirai.group;

import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import lombok.Getter;

@Getter
public class MemberSpecialTitleChangeEvent extends GroupEvent {
    private String origin;
    private String current;
    private MiraiSender member;
}
