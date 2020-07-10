package cc.sfclub.mirai.events.mirai.group.bot;

import cc.sfclub.mirai.events.mirai.group.GroupEvent;
import lombok.Getter;

@Getter
public class BotGroupPermissionChangeEvent extends GroupEvent implements BotEvent {
    private String origin;
    private String current;
}
