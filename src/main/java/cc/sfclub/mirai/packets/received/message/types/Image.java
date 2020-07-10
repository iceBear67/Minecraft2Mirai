package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;
import lombok.Getter;

/**
 * 三个参数任选其一，出现多个参数时，按照imageId > url > file的优先级
 */
@Getter
@Builder
public class Image extends MiraiTypeMessage {
    private String imageId;
    private String url;
    @Deprecated
    private String path;
}
