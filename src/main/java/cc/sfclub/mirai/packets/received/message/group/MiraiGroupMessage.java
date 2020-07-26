package cc.sfclub.mirai.packets.received.message.group;

import cc.sfclub.core.Core;
import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import cc.sfclub.mirai.packets.received.message.types.Source;
import cc.sfclub.mirai.packets.received.sender.MiraiGroupSender;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class MiraiGroupMessage extends MiraiMessage {

    private transient List<MiraiTypeMessage> messageChain = new ArrayList<>();
    private MiraiGroupSender sender;

    public int getMessageId() {
        if (messageChain.isEmpty() || !(messageChain.get(0) instanceof Source)) return -1;
        return ((Source) messageChain.get(0)).getId();
    }

    @SneakyThrows
    public static Optional<MiraiGroupMessage> parseJson(String json) {
        MiraiGroupMessage origin = Core.getGson().fromJson(json, MiraiGroupMessage.class);
        JsonElement element = JsonParser.parseString(json);
        if (!element.getAsJsonObject().has("messageChain")) return Optional.empty();
        JsonArray messageChain = element.getAsJsonObject().get("messageChain").getAsJsonArray();
        messageChain.forEach(j -> {
            String type = j.getAsJsonObject().get("type").getAsString();
            try {
                Class clazz = Class.forName("cc.sfclub.mirai.packets.received.message.types." + type);
                origin.messageChain.add((MiraiTypeMessage) Core.getGson().fromJson(j, clazz));
            } catch (ClassNotFoundException e) {
                Core.getLogger().error("Unsupported type:{}", type);
            }
        });
        return Optional.of(origin);
    }
}
