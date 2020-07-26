package cc.sfclub.mirai.events.mirai.group.bot;

import cc.sfclub.mirai.packets.received.sender.MiraiGroupSender;
import lombok.Getter;

@Getter
public class BotMuteEvent implements BotEvent {
    private int durationSeconds;
    private MiraiGroupSender operator;
}
