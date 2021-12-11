package cc.sfclub.mirai.packets.received.message.group;

import cc.sfclub.mirai.adapts.SBSpigot;
import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import cc.sfclub.mirai.packets.received.message.types.Source;
import cc.sfclub.mirai.packets.received.sender.MiraiGroupSender;
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
public class MiraiGroupMessage extends MiraiMessage {
    private transient static final Gson gson = new Gson();
    private transient static final Logger logger = LoggerFactory.getLogger(MiraiGroupMessage.class);
    private transient List<MiraiTypeMessage> messageChain = new ArrayList<>();
    private MiraiGroupSender sender;

    public int getMessageId() {
        if (messageChain.isEmpty() || !(messageChain.get(0) instanceof Source)) return -1;
        return ((Source) messageChain.get(0)).getId();
    }

    @SneakyThrows
    public static Optional<MiraiGroupMessage> parseJson(String json) {
        MiraiGroupMessage origin = AdaptedJsonParser.adaptedSerialize(json, MiraiGroupMessage.class);
        JsonElement element = AdaptedJsonParser.parseString(json);
        if (!element.getAsJsonObject().has("messageChain")) return Optional.empty();
        JsonArray messageChain = element.getAsJsonObject().get("messageChain").getAsJsonArray();
        origin.messageChain = MessageUtil.deserializeJsonMessageChain(messageChain);
        return Optional.of(origin);
    }
}
