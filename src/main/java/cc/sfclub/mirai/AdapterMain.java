package cc.sfclub.mirai;

import cc.sfclub.command.Source;
import cc.sfclub.core.Core;
import cc.sfclub.events.message.group.GroupMessageReceivedEvent;
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
import cc.sfclub.user.User;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.nutz.dao.Cnd;

import java.util.Base64;
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
    private static final EventBus MiraiEventBus = EventBus.builder().logNoSubscriberMessages(false).build();
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
        Core.get().dispatcher().register(
                LiteralArgumentBuilder.<Source>literal("mirai")
                        .requires(source -> {
                            User user = Core.get().ORM().fetch(User.class, Cnd.where("UniqueID", "=", source.getMessageEvent().getUserID()));
                            if (user == null) {
                                return false;
                            }
                            return user.hasPermission("mirai.admin");
                        })
                        .then(LiteralArgumentBuilder.<Source>literal("image")
                                .executes(c -> {
                                    if (c.getSource().getMessageEvent() instanceof GroupMessageReceivedEvent) {
                                        GroupMessageReceivedEvent m = (GroupMessageReceivedEvent) c.getSource().getMessageEvent();
                                        m.getGroup().reply(m.getMessageID(), "[Image:" + Base64.getUrlEncoder().encodeToString("https://i.loli.net/2020/07/11/RuBwdh89AezLHUO.jpg".getBytes()) + "]");
                                    }
                                    return 0;
                                })
                        )
                        .then(LiteralArgumentBuilder.<Source>literal("at")
                                .executes(c -> {
                                            if (c.getSource().getMessageEvent() instanceof GroupMessageReceivedEvent) {
                                                GroupMessageReceivedEvent m = (GroupMessageReceivedEvent) c.getSource().getMessageEvent();
                                                m.getGroup().reply(m.getMessageID(), "[At:" + m.getUserID() + "]");
                                            }
                                            return 0;
                                        }
                                ))
                        .executes(c -> {
                            if (c.getSource().getMessageEvent() instanceof GroupMessageReceivedEvent) {
                                GroupMessageReceivedEvent m = (GroupMessageReceivedEvent) c.getSource().getMessageEvent();
                                m.getGroup().reply(m.getMessageID(), "MiraiAdapter Running!");
                            }
                            return 0;
                        })
        );
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
            wsEventListener.cancel();
        }
        if (wsMessageListener != null) {
            wsMessageListener.close(1000, "onDisable");
            wsMessageListener.cancel();
        }
    }
}
