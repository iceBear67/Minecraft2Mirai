package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import lombok.Builder;

@Builder
public class FlashImage extends MiraiMessage {
    private String imageId;
    private String url;
    private String path;
}
