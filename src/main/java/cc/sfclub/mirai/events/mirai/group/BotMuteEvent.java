package cc.sfclub.mirai.events.mirai.group;

import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import lombok.Getter;

/**
 * 需要注意的是，他作为GroupEvent的子类只是为了多态兼容性，要获取group请使用operator.group
 */
@Getter
public class BotMuteEvent extends GroupEvent {
    private int durationSeconds;
    private MiraiSender operator;
}
