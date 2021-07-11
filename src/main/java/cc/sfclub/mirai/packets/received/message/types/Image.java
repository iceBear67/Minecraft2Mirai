package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;
import lombok.Getter;

/**
 */
@Getter
@Builder
public class Image extends MiraiTypeMessage {
    private String imageId;
    private String url;
    @Deprecated
    private String path;
}
