package cc.sfclub.mirai.packets.received.message.contact;

import cc.sfclub.mirai.packets.received.contact.MiraiContact;
import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import cc.sfclub.mirai.packets.received.message.types.Source;
import cc.sfclub.mirai.utils.AdaptedJsonParser;
import cc.sfclub.mirai.utils.MessageUtil;
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
        MiraiContactMessage origin = AdaptedJsonParser.adaptedSerialize(json, MiraiContactMessage.class);
        JsonElement element = AdaptedJsonParser.parseString(json);
        if (!element.getAsJsonObject().has("messageChain")) return Optional.empty();
        JsonArray messageChain = element.getAsJsonObject().get("messageChain").getAsJsonArray();
        origin.messageChain = MessageUtil.deserializeJsonMessageChain(messageChain);
        return Optional.of(origin);
    }

    public int getMessageId() {
        if (messageChain.isEmpty() || !(messageChain.get(0) instanceof Source)) return -1;
        return ((Source) messageChain.get(0)).getId();
    }
}
