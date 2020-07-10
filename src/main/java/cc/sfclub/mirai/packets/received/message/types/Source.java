package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Source extends MiraiTypeMessage {
    private int id;
    private int time;
}
