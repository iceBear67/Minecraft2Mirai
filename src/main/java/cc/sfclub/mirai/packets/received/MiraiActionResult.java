package cc.sfclub.mirai.packets.received;

import lombok.Getter;

@Getter
public class MiraiActionResult extends MiraiEvent {
    /**
     */
    private String id;
    /**
     */
    private boolean success;

    @Getter
    class Result {
        private String error;
        private String errorDetail;
    }
}
