package cc.sfclub.mirai;

public interface Receiver {
    /**
     * Send a message
     *
     * @param message message
     */
    void sendMessage(String message);

    /**
     * reply the message
     *
     * @param messageId target
     * @param message   message
     */
    void reply(long messageId, String message);
}
