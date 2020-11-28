package cc.sfclub.mirai;

import cc.sfclub.core.Core;
import cc.sfclub.events.Event;
import cc.sfclub.events.message.direct.PrivateMessage;
import cc.sfclub.events.message.group.GroupMessage;
import cc.sfclub.mirai.bot.QQBot;
import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import cc.sfclub.mirai.packets.received.message.contact.MiraiContactMessage;
import cc.sfclub.mirai.packets.received.message.group.MiraiGroupMessage;
import cc.sfclub.mirai.utils.MessageUtil;
import cc.sfclub.user.User;
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
        AdapterMain.get(AdapterMain.class).requestReconnect();
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
        AdapterMain.get(AdapterMain.class).reconnectCounter = 0;
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
                User u = Core.get().userManager().byPlatformID(QQBot.PLATFORM_NAME, String.valueOf(miraiGroupMessage.getSender().getId()));
                if (u == null) {
                    if (!Config.getInst().autoCreateAccount) {
                        return;
                    }
                    u = Core.get().userManager().register(Core.get().permCfg().getDefaultGroup(), QQBot.PLATFORM_NAME, String.valueOf(miraiGroupMessage.getSender().getId()));
                }
                //checks..
                synchronized (this) {
                    QQBot bot = (QQBot) Core.get().bot("QQ").get();
                    if (bot.getGroup(miraiGroupMessage.getSender().getGroup().getId()).isEmpty()) {
                        AdapterMain.get(AdapterMain.class).refreshGroup(
                                miraiGroupMessage.getSender().getGroup()
                        );
                    }
                }
                GroupMessage groupMessage = new GroupMessage(
                        u.getUniqueID(),
                        MessageUtil.deserializeChain(miraiGroupMessage.getMessageChain()),
                        miraiGroupMessage.getSender().getGroup().getId(),
                        QQBot.PLATFORM_NAME,
                        miraiGroupMessage.getMessageId()
                );

                Event.postEvent(groupMessage);
                if (Config.getInst().displayMessage) {
                    logger.info("[MiraiAdapter] [{}({})] {} -> {}",
                            miraiGroupMessage.getSender().getGroup().getName(),
                            groupMessage.getGroupId(),
                            miraiGroupMessage.getSender().getMemberName(),
                            groupMessage.getMessage());
                }
                break;
            case "FriendMessage":
            case "TempMessage":
                MiraiContactMessage.parseJson(text).ifPresent(Msg -> {
                    AdapterMain.getMiraiEventBus().post(Msg);
                    User u3 = Core.get().userManager().byPlatformID(QQBot.PLATFORM_NAME, String.valueOf(Msg.getSender().getId()));
                    if (u3 == null) {
                        if (!Config.getInst().autoCreateAccount) {
                            return;
                        }
                        u3 = Core.get().userManager().register(Core.get().permCfg().getDefaultGroup(), QQBot.PLATFORM_NAME, String.valueOf(Msg.getSender().getId()));
                    }
                    PrivateMessage privateMessage = new PrivateMessage(
                            u3.getUniqueID(),
                            MessageUtil.deserializeChain(Msg.getMessageChain()),
                            QQBot.PLATFORM_NAME,
                            Msg.getMessageId()
                    );
                    Event.postEvent(privateMessage);
                    if (Config.getInst().displayMessage) {
                        logger.info("[MiraiAdapter] [CONTACT] [{}({})] ->{}",
                                Msg.getSender().getRemark(),
                                Msg.getSender().getId(),
                                privateMessage.getMessage());
                    }
                });
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
