package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Xml extends MiraiTypeMessage {
    private String xml;
}
