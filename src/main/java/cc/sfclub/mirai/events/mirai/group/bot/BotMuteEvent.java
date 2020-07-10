package cc.sfclub.mirai.events.mirai.group.bot;

import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import lombok.Getter;

@Getter
public class BotMuteEvent implements BotEvent {
    private int durationSeconds;
    private MiraiSender operator;
}
