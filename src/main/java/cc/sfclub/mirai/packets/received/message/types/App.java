package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class App extends MiraiMessage {
    private String content;
}
