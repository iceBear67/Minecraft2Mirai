package cc.sfclub.mirai;

import cc.sfclub.mirai.adapts.GroupMessage;
import cc.sfclub.mirai.bot.QQBot;
import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import cc.sfclub.mirai.packets.received.message.contact.MiraiContactMessage;
import cc.sfclub.mirai.packets.received.message.group.MiraiGroupMessage;
import cc.sfclub.mirai.utils.MessageUtil;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsListener extends WebSocketListener {
    private static final Gson gson = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(WsListener.class);

    public WsListener() {
        logger.info("WsListener Started!");
    }

    @SneakyThrows
    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        logger.error("[MiraiAdapter] WebSocket Connection has a exception:{}", t.getMessage());
        t.printStackTrace();
        AdapterMain.getPlugin(AdapterMain.class).authed = false;
        AdapterMain.getPlugin(AdapterMain.class).requestReconnect();
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosing(webSocket, code, reason);
        if (reason.equals("onDisable")) {
            logger.info("WsListener shutting down...");
            return;
        }
        logger.warn("[MiraiAdapter] Connection closing!! Reason:{}", reason);
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        AdapterMain.getPlugin(AdapterMain.class).reconnectCounter = 0;
        logger.info("WsListener Connected!");
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
        MiraiMessage message = gson.fromJson(text, MiraiMessage.class);
        if (MessageUtil.isMiraiEvent(message.getType())) {
            onELMessage(webSocket, text);
            return;
        }
        switch (message.getType()) {
            case "GroupMessage":
                MiraiGroupMessage miraiGroupMessage = MiraiGroupMessage.parseJson(text).orElseThrow(IllegalArgumentException::new);
                AdapterMain.getMiraiEventBus().post(miraiGroupMessage);
                //checks..
                synchronized (this) {
                    QQBot bot = (QQBot) AdapterMain.getPlugin(AdapterMain.class).getBot();
                    if (!bot.getGroup(miraiGroupMessage.getSender().getGroup().getId()).isPresent()) {
                        AdapterMain.getPlugin(AdapterMain.class).refreshGroup(
                                miraiGroupMessage.getSender().getGroup()
                        );
                    }
                }
                break;
            case "FriendMessage":
                break;
        }
    }

    public void onELMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        MiraiMessage message = gson.fromJson(text, MiraiMessage.class);
        if (!MessageUtil.isMiraiEvent(message.getType())) return;
        String type = message.getType();
        try {
            Class<?> clazz = null;
            if (type.contains("Friend")) {
                clazz = Class.forName("cc.sfclub.mirai.events.mirai.friend." + type);
            } else if (type.startsWith("Group")) {
                clazz = Class.forName("cc.sfclub.mirai.events.mirai.group." + type);
            } else if (type.startsWith("Member")) {
                clazz = Class.forName("cc.sfclub.mirai.events.mirai.group.member." + type);
            } else if (type.startsWith("Bot")) {
                try {
                    clazz = Class.forName("cc.sfclub.mirai.events.mirai.group.bot." + type);
                } catch (ClassNotFoundException e) {
                    clazz = Class.forName("cc.sfclub.mirai.events.mirai.bot." + type);
                }
            }
            if (clazz != null) {
                AdapterMain.getMiraiEventBus().post(gson.fromJson(text, clazz));
            }
        } catch (ClassNotFoundException e) {
            logger.error("Couldn't find class {} for event!", type, e);
        }
    }
}
