package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Plain extends MiraiMessage {
    private String text;
}
