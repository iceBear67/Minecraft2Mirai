package cc.sfclub.mirai.events.mirai.bot;

import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import lombok.Getter;

@Getter
public abstract class BotEvent extends MiraiMessage {
    private long qq;
}
