package cc.sfclub.mirai.adapts;

import cc.sfclub.mirai.AdapterMain;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * When a group message was received
 */

public class GroupMessage extends Message {
    @Getter
    private final long groupId;
    @Getter
    private final ChatGroup group;

    public GroupMessage(String userID, String message, long group, String transform, long messageID) {
        super(userID, message, transform, messageID);
        this.groupId = group;
        this.group= AdapterMain.getPlugin(AdapterMain.class).getBot().getGroup(groupId).orElseThrow(AssertionError::new);
    }

    @Override
    public void reply(long msgId, String message) {
        group.reply(msgId, message);
    }

    public void reply(String message) {
        group.reply(super.getMessageID(), message);
    }

    public ChatGroup getChatGroup() {
        return getTransformAsBot().getGroup(groupId).orElseThrow(NullPointerException::new);
    }

    public Bot getTransformAsBot() {
        return AdapterMain.getPlugin(AdapterMain.class).getBot();
    }

}
