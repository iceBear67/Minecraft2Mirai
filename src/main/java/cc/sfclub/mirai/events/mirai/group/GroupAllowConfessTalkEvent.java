package cc.sfclub.mirai.events.mirai.group;

import lombok.Getter;

@Getter
public class GroupAllowConfessTalkEvent extends GroupEvent {
    private boolean origin;
    private boolean current;
    private boolean isByBot;
}
