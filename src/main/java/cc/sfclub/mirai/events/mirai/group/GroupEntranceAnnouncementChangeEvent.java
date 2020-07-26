package cc.sfclub.mirai.events.mirai.group;

import cc.sfclub.mirai.packets.received.sender.MiraiGroupSender;
import lombok.Getter;

@Getter
public class GroupEntranceAnnouncementChangeEvent extends GroupEvent {
    private String origin;
    private String current;
    private MiraiGroupSender operator;
}
