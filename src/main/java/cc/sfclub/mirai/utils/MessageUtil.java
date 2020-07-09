package cc.sfclub.mirai.utils;

import cc.sfclub.mirai.packets.received.message.MiraiMessage;

import java.util.ArrayList;
import java.util.List;

public class MessageUtil {
    public static MessageChainBuilder buildChain() {
        return new MessageChainBuilder();
    }

    public static class MessageChainBuilder {
        private List<MiraiMessage> messageChain = new ArrayList<>();

        public List<MiraiMessage> build() {
            return messageChain;
        }

        public MessageChainBuilder append(MiraiMessage m) {
            messageChain.add(m);
            return this;
        }
    }
}
