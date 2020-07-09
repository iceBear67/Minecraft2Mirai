package cc.sfclub.mirai.events.mirai.group;

import lombok.Getter;

@Getter
public class BotGroupPermissionChangeEvent extends GroupEvent {
    private String origin;
    private String current;
}
