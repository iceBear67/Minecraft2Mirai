package cc.sfclub.mirai.packets.received.message.contact;

import cc.sfclub.core.Core;
import cc.sfclub.mirai.packets.received.contact.MiraiContact;
import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import cc.sfclub.mirai.packets.received.message.types.Source;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class MiraiContactMessage extends MiraiMessage {

    private transient List<MiraiTypeMessage> messageChain = new ArrayList<>();
    private MiraiContact sender;

    @SneakyThrows
    public static Optional<MiraiContactMessage> parseJson(String json) {
        MiraiContactMessage origin = Core.getGson().fromJson(json, MiraiContactMessage.class);
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

    public int getMessageId() {
        if (messageChain.isEmpty() || !(messageChain.get(0) instanceof Source)) return -1;
        return ((Source) messageChain.get(0)).getId();
    }
}
