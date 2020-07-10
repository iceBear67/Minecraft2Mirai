package cc.sfclub.mirai.events.mirai.group.bot;

import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import lombok.Getter;

@Getter
public class BotUnmuteEvent implements BotEvent {
    private MiraiSender operator;
}
