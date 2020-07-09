package cc.sfclub.mirai.packets.received.message.group;

import cc.sfclub.mirai.packets.received.MiraiEvent;
import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import lombok.Getter;

import java.util.List;

@Getter
public class MiraiGroupMessage extends MiraiEvent {
    private List<MiraiMessage> messageChain;
    private MiraiSender sender;
}
