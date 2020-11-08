package cc.sfclub.mirai.utils;

import cc.sfclub.catcode.CatCodeHelper;
import cc.sfclub.core.Core;
import cc.sfclub.mirai.bot.QQBot;
import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import cc.sfclub.mirai.packets.received.message.types.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class MessageUtil {
    private transient static final Gson gson = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(MessageUtil.class);
    public static boolean isMiraiEvent(String type) {
        if (type.endsWith("Event")) return true;
        if (type.startsWith("BotOfflineEvent")) return true;
        return false;
    }

    public static String deserializeChain(List<MiraiTypeMessage> chain) {
        StringBuilder cctext = new StringBuilder();
        chain.forEach(c -> {
            cctext.append(deserialzeTypeMessage(c));
        });
        return cctext.toString();
    }

    public static String deserialzeTypeMessage(MiraiTypeMessage message) {
        StringBuilder builder = new StringBuilder();
        switch (message.getType()) {
            case "Source":
                return "";//We don't need it in catcodes.
            case "At":
                long qquin = ((At) message).getTarget();
                builder.append("[At:").append(Core.get().userManager().byPlatformID(QQBot.PLATFORM_NAME, String.valueOf(qquin)).getUniqueID()).append(']');
                return builder.toString();
            case "AtAll":
                return "[AtAll]";
            case "Image":
                Image image = (Image) message;
                builder.append("[Image:").append(Base64.getUrlEncoder().encodeToString(image.getUrl().getBytes())).append(']');
                return builder.toString();
            case "Plain":
                return ((Plain) message).getText();
            /*case "Quote":
                Quote quote = (Quote) message;
                return deserializeChain(quote.getOrigin());*/
        }
        if (Core.get().config().isDebug()) {
            logger.warn("[MiraiAdapter] Unsupported message: {}", message.getType());
        }
        return "";
    }

    public static List<MiraiTypeMessage> deserializeCatCodes(String catcode) {
        List<MiraiTypeMessage> messages = new ArrayList<>();
        for (String s : CatCodeHelper.spilt(catcode)) {
            messages.add(deserializeSingleCatCode(s));
        }
        return messages;
    }

    public static MiraiTypeMessage deserializeSingleCatCode(String catcode) {
        if (!catcode.startsWith("[")) {
            return Plain.builder().text(catcode).build();
        }
        String code = catcode.replaceFirst("\\[", "");
        code = code.substring(0, code.length() - 1);//去掉最后面那个]
        String[] args = code.split(":");
        if (args.length != 2) {
            if ("AtAll".equals(code)) return AtAll.INST;
            return Plain.builder().text(catcode).build();
        }
        switch (args[0]) {
            case "At":
                String userId = args[1];
                return At.builder().target(Long.parseLong(Core.get().userManager().byUUID(userId).getPlatformId())).build();
            case "Plain":
                return Plain.builder().text(args[1]).build();
            case "Image":
                return Image.builder().url(new String(Base64.getUrlDecoder().decode(args[1]))).build();
        }
        logger.warn("Unsupported type: {}", args[0]);
        return Plain.builder().text(catcode).build();
    }

    public static MessageChainBuilder buildChain() {
        return new MessageChainBuilder();
    }

    public static class MessageChainBuilder {
        private List<MiraiTypeMessage> messageChain = new ArrayList<>();

        public List<MiraiTypeMessage> build() {
            return messageChain;
        }

        public MessageChainBuilder append(MiraiTypeMessage m) {
            messageChain.add(m);
            return this;
        }
    }

    public static List<MiraiTypeMessage> deserializeJsonMessageChain(JsonArray messageChain) {
        List<MiraiTypeMessage> result = new LinkedList<>();
        messageChain.forEach(j -> {
            String type = j.getAsJsonObject().get("type").getAsString();
            try {
                Class clazz = Class.forName("cc.sfclub.mirai.packets.received.message.types." + type);
                MiraiTypeMessage msg = (MiraiTypeMessage) gson.fromJson(j, clazz);
                if (msg instanceof Quote) {
                    Quote quote = (Quote) msg;
                    List<MiraiTypeMessage> sub = deserializeJsonMessageChain(j.getAsJsonObject().getAsJsonArray("origin"));
                    result.add(Quote.builder().groupId(quote.getGroupId())
                            .id(quote.getId())
                            .origin(sub)
                            .senderId(quote.getSenderId())
                            .targetId(quote.getTargetId())
                            .build());
                } else {
                    result.add(msg);
                }
            } catch (ClassNotFoundException e) {
                logger.error("Unsupported type:{}", type);
            }
        });
        return result;
    }
}
