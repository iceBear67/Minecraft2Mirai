package cc.sfclub.mirai.packets.received.message.contact;

import cc.sfclub.mirai.packets.received.contact.MiraiContact;
import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import cc.sfclub.mirai.packets.received.message.types.Source;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class MiraiContactMessage extends MiraiMessage {
    private transient static final Gson gson = new Gson();
    private transient static final Logger logger = LoggerFactory.getLogger(MiraiContactMessage.class);
    private transient List<MiraiTypeMessage> messageChain = new ArrayList<>();
    private MiraiContact sender;

    @SneakyThrows
    public static Optional<MiraiContactMessage> parseJson(String json) {
        MiraiContactMessage origin = gson.fromJson(json, MiraiContactMessage.class);
        JsonElement element = JsonParser.parseString(json);
        if (!element.getAsJsonObject().has("messageChain")) return Optional.empty();
        JsonArray messageChain = element.getAsJsonObject().get("messageChain").getAsJsonArray();
        messageChain.forEach(j -> {
            String type = j.getAsJsonObject().get("type").getAsString();
            try {
                Class clazz = Class.forName("cc.sfclub.mirai.packets.received.message.types." + type);
                origin.messageChain.add((MiraiTypeMessage) gson.fromJson(j, clazz));
            } catch (ClassNotFoundException e) {
                logger.error("Unsupported type:{}", type);
            }
        });
        return Optional.of(origin);
    }

    public int getMessageId() {
        if (messageChain.isEmpty() || !(messageChain.get(0) instanceof Source)) return -1;
        return ((Source) messageChain.get(0)).getId();
    }
}
