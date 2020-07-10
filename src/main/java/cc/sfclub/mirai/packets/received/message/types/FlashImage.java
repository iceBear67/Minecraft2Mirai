package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FlashImage extends MiraiTypeMessage {
    private String imageId;
    private String url;
    private String path;

}
