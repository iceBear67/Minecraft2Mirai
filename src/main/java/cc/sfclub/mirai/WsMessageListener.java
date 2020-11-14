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

public class WsMessageListener extends WebSocketListener {
    private static final Gson gson = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(WsEventListener.class);

    public WsMessageListener() {
        logger.info("WsMessageListener Started!");
    }

    @SneakyThrows
    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        logger.error("[MiraiAdapter] WebSocket Connection has a exception:{}", t.getMessage());
        t.printStackTrace();
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosing(webSocket, code, reason);
        if (reason.equals("onDisable")) {
            logger.info("WsMessageListener shutting down...");
            return;
        }
        logger.warn("[MiraiAdapter] Connection closing!! Reason:{}", reason);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
        MiraiMessage message = gson.fromJson(text, MiraiMessage.class);
        if (MessageUtil.isMiraiEvent(message.getType())) return;
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
            case "PrivateMessage":
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
                                Msg.getSender().getNickname(),
                                Msg.getSender().getId(),
                                privateMessage.getMessage());
                    }
                });
                break;
        }
    }
}
