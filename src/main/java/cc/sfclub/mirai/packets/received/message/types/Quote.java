package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Quote extends MiraiTypeMessage {
    private int id;
    private long groupId;
    private long senderId;
    private long targetId;
    private List<MiraiTypeMessage> origin;
}
