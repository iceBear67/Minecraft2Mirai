package cc.sfclub.mirai.packets.received.message.group;

import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import cc.sfclub.mirai.packets.received.message.types.Source;
import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import lombok.Getter;

import java.util.List;

@Getter
public class MiraiGroupMessage extends MiraiMessage {
    private List<MiraiTypeMessage> messageChain;
    private MiraiSender sender;

    public int getMessageId() {
        if (messageChain.isEmpty() || !(messageChain.get(0) instanceof Source)) return -1;
        return ((Source) messageChain.get(0)).getId();
    }
}
