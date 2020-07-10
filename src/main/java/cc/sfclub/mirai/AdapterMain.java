package cc.sfclub.mirai;

import cc.sfclub.core.Core;
import cc.sfclub.events.server.ServerStartedEvent;
import cc.sfclub.events.server.ServerStoppingEvent;
import cc.sfclub.mirai.bot.QQBot;
import cc.sfclub.mirai.bot.QQContact;
import cc.sfclub.mirai.bot.QQGroup;
import cc.sfclub.mirai.misc.UIDMap;
import cc.sfclub.mirai.packets.*;
import cc.sfclub.plugin.Plugin;
import cc.sfclub.transform.Bot;
import cc.sfclub.transform.Contact;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;
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
    private static final EventBus MiraiEventBus = EventBus.builder().build();
    @Getter
    private WebSocket wsEventListener;
    @Getter
    private WebSocket wsMessageListener;

    @Subscribe
    public void onServerStart(ServerStartedEvent e) {
        Core.getLogger().info("Mirai-Adapter loading");
        Config conf = new Config(getDataFolder().toString());
        Config.setInst((Config) conf.saveDefaultOrLoad());
        if (!Core.get().ORM().exists(UIDMap.class)) {
            Core.get().ORM().create(UIDMap.class, false);
            Core.getLogger().info("Creating User-UIN Relation Table");
        }
        Core.getLogger().info("Try logging in..");
        //Init database
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
                    wsMessageListener = httpClient.newWebSocket(request, new WsMessageListener());
                    Request _request = new Request.Builder()
                            .url(Config.getInst().baseUrl.replaceAll("http", "ws").concat("event?sessionKey=").concat(Cred.sessionKey))
                            .addHeader("Sec-Websocket-Key", UUID.randomUUID().toString())
                            .build();
                    wsEventListener = httpClient.newWebSocket(_request, new WsEventListener());
                });
        if (Cred.sessionKey == null)
            Core.getLogger().warn("Failed to get session. Response: {}", auth.getRawResponse());
        //load groups
        Core.get().registerBot(new QQBot());
        Bot bot = Core.get().bot("QQ").orElseThrow(() -> new IllegalArgumentException("Unknown error"));
        GroupList.builder().sessionKey(Cred.sessionKey).build().send().asGroups().forEach(g -> {
            Set<Contact> contactSet = new HashSet<>();
            //load group members
            GroupMemberList.builder().sessionKey(Cred.sessionKey)
                    .target(g.getId())
                    .build().send().asList().forEach(ms -> {
                QQContact contact = new QQContact(ms.getId(), ms.getMemberName(), ms.getPermission());
                contactSet.add(contact);
                bot.addContact(contact, true);
            });
            QQGroup group = new QQGroup(g.getId(), contactSet, g.getName());
            bot.addGroup(group, true);
        });
        //EventBus.getDefault().register(new GroupMessageWrapper());

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
        if (wsEventListener != null) {
            wsEventListener.close(1000, "onDisable");
        }
        if (wsMessageListener != null) {
            wsMessageListener.close(1000, "onDisable");
        }
    }
}
