package cc.sfclub.mirai;

import cc.sfclub.core.Core;
import cc.sfclub.events.message.group.GroupMessageReceivedEvent;
import cc.sfclub.mirai.misc.UIDMap;
import cc.sfclub.mirai.packets.received.message.MiraiMessage;
import cc.sfclub.mirai.packets.received.message.group.MiraiGroupMessage;
import cc.sfclub.mirai.utils.MessageUtil;
import lombok.SneakyThrows;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WsMessageListener extends WebSocketListener {
    @SneakyThrows
    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        Core.getLogger().error("[MiraiAdapter] WebSocket Connection has a exception:{}", t.getMessage());
        throw t;
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
        MiraiMessage message = Core.getGson().fromJson(text, MiraiMessage.class);
        if (MessageUtil.isMiraiEvent(message.getType())) return;
        switch (message.getType()) {
            case "GroupMessage":
                MiraiGroupMessage miraiGroupMessage = Core.getGson().fromJson(text, MiraiGroupMessage.class);
                AdapterMain.getMiraiEventBus().post(miraiGroupMessage);
                GroupMessageReceivedEvent groupMessage = new GroupMessageReceivedEvent(
                        UIDMap.fromQQUIN(miraiGroupMessage.getSender().getId())
                                .orElseThrow(IllegalArgumentException::new)
                                .getUserID(),
                        MessageUtil.deserializeChain(miraiGroupMessage.getMessageChain()),
                        miraiGroupMessage.getSender().getGroup().getId(),
                        "QQ",
                        miraiGroupMessage.getMessageId()
                );
                EventBus.getDefault().post(groupMessage);
                if (Config.getInst().displayMessage) {
                    Core.getLogger().info("[MiraiAdapter] Group:{} Message:{}", groupMessage.getGroupId(), groupMessage.getMessage());
                }
                break;
        }
    }
}
