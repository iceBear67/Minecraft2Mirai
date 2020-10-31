package cc.sfclub.mirai.packets.received;

import lombok.Getter;

@Getter
public class MiraiActionResult extends MiraiEvent {
    /**
     * 给客户端发送的识别ID
     */
    private String id;
    /**
     * 是否成功
     */
    private boolean success;

    @Getter
    class Result {
        private String error;
        private String errorDetail;
    }
}
