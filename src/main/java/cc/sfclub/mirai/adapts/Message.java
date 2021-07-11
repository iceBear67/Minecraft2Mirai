package cc.sfclub.mirai.adapts;


/**
 * When message was received
 */

public class Message extends MessageEvent {

    public Message(String userID, String message, String transform, long messageID) {
        super(userID, message, transform, messageID);
    }
}
