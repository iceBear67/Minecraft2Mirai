package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Plain extends MiraiTypeMessage {
    private String text;
}
