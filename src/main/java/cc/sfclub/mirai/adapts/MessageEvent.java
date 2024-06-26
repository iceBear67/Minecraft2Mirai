package cc.sfclub.mirai.adapts;


import lombok.Getter;

public class MessageEvent extends PlatformEvent {
    @Getter
    private final String UserID;
    @Getter
    private final String message;
    @Getter
    private final long messageID;
    @Getter
    private final String transform;

    public MessageEvent(String userID, String message, String transform, long messageID) {
        this.UserID = userID;
        this.message = message;
        this.transform = transform;
        this.messageID = messageID;
    }

    public void reply(long msgId, String message) {
        throw new IllegalArgumentException("REPLY IS NOT SUPPORTED");
    }

    public void reply(String message) {
        throw new IllegalArgumentException("REPLY IS NOT SUPPORTED");
    }
}
