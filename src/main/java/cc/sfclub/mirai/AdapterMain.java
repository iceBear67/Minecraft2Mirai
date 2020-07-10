package cc.sfclub.mirai;

import cc.sfclub.core.Core;
import cc.sfclub.events.server.ServerStartedEvent;
import cc.sfclub.events.server.ServerStoppingEvent;
import cc.sfclub.mirai.packets.Auth;
import cc.sfclub.mirai.packets.Release;
import cc.sfclub.mirai.packets.Verify;
import cc.sfclub.mirai.utils.MessageUtil;
import cc.sfclub.plugin.Plugin;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AdapterMain extends Plugin {
    @Getter
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
    @Getter
    private static final EventBus MiraiEventBUs = EventBus.builder().build();
    @Getter
    private WebSocket webSocket;

    @Subscribe
    public void onServerStart(ServerStartedEvent e) {
        Core.getLogger().info("Mirai-Adapter loading");
        Config conf = new Config(getDataFolder().toString());
        Config.setInst((Config) conf.saveDefaultOrLoad());
        Core.getLogger().info("Try logging in..");

        Auth auth = Auth.builder()
                .authKey(Config.getInst().authKey)
                .build();
        auth.send()
                .asSession()
                .ifPresent(s -> {
                    Core.getLogger().info("[MiraiAdapter] Logged in!");
                    Cred.sessionKey = s;
                    String response = Verify.builder().qq(Config.getInst().QQ)
                            .sessionKey(Cred.sessionKey)
                            .build()
                            .send()
                            .getRawResponse();
                    Core.getLogger().info("[MiraiAdapter] Try verify: {}", response);
                    Request request = new Request.Builder()
                            .url(Config.getInst().baseUrl.replaceAll("http", "ws").concat("message?sessionKey=").concat(Cred.sessionKey))
                            .addHeader("Sec-Websocket-Key", UUID.randomUUID().toString())
                            .build();
                    Core.getLogger().info("[MiraiAdapter] Connecting to {}", Config.getInst().baseUrl.replaceAll("http", "ws"));
                    webSocket = httpClient.newWebSocket(request, new WebsocketListener());
                });
        if (Cred.sessionKey == null)
            Core.getLogger().warn("Failed to get session. Response: {}", auth.getRawResponse());
        MessageUtil.isMiraiEvent("");
    }

    @Subscribe
    public void onServerStop(ServerStoppingEvent e) {
        Core.getLogger().info("Logging out..");
        Core.getLogger().info("Releasing session: {}", Release.builder()
                .qq(Config.getInst().QQ)
                .sessionKey(Cred.sessionKey)
                .build()
                .send()
                .asMessage().orElse("Unknown Error")
        );
        if (webSocket != null) {
            webSocket.close(1000, "onDisable");
        }
    }
}
