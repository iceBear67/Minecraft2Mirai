package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Face extends MiraiTypeMessage {
    private int faceId;
    private String name;
}
