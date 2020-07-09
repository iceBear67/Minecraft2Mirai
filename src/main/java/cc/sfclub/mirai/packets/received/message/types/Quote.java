package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Quote extends MiraiMessage {
    private int id;
    private long groupId;
    private long senderId;
    private long targetId;
    private List<MiraiMessage> origin;
}
