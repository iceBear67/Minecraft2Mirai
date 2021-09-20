package cc.sfclub.mirai;

import cc.sfclub.command.Source;
import cc.sfclub.core.Core;
import cc.sfclub.events.MessageEvent;
import cc.sfclub.events.server.ServerStartedEvent;
import cc.sfclub.mirai.bot.QQBot;
import cc.sfclub.mirai.bot.QQContact;
import cc.sfclub.mirai.bot.QQGroup;
import cc.sfclub.mirai.packets.*;
import cc.sfclub.mirai.packets.received.sender.MiraiGroup;
import cc.sfclub.plugin.Plugin;
import cc.sfclub.transform.Bot;
import cc.sfclub.transform.Contact;
import cc.sfclub.user.perm.Perm;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.Getter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdapterMain extends Plugin {
    @Getter
    private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.of(6, ChronoUnit.SECONDS)).build();
    @Getter
    private static final EventBus MiraiEventBus = EventBus.builder().sendNoSubscriberEvent(false).logNoSubscriberMessages(false).build();
    @Getter
    private WebSocket wsMessageListener;
    @Getter
    private ExecutorService threadPool = Executors.newFixedThreadPool(4);
    protected int reconnectCounter = 0;
    protected boolean authed = false;

    @Subscribe
    @SuppressWarnings("all")
    public void onServerStart(ServerStartedEvent e) throws URISyntaxException {
        getLogger().info("Mirai-Adapter loading");
        getConfig().saveDefault();
        getConfig().reloadConfig();
        Config.setInst((Config) getConfig().get());
        getLogger().info("Try logging in..");
        //Init database
        Auth auth = Auth.builder()
                .verifyKey(Config.getInst().authKey)
                .build();
        auth.send()
                .asSession()
                .ifPresent(s -> {
                    getLogger().info("[MiraiAdapter] Logged in!");
                    authed = true;
                    Cred.sessionKey = s;
                    String response = Bind.builder().qq(Config.getInst().QQ)
                            .sessionKey(Cred.sessionKey)
                            .build()
                            .send()
                            .getRawResponse();
                    getLogger().info("[MiraiAdapter] Try verify: {}", response);
                    //load groups
                    Core.get().registerBot(new QQBot());
                    Bot bot = Core.get().bot(QQBot.PLATFORM_NAME).orElseThrow(() -> new IllegalArgumentException("Unknown error"));
                    refreshContacts();
                    threadPool.submit(() -> {
                        try {
                            Thread.sleep(300 * 1000);
                        } catch (InterruptedException ignored) {

                        }
                        if (this.isLoaded())
                            refreshContacts();
                    });
                    try {
                        var url = Config.getInst().baseUrl.replaceAll("http", "ws").concat("all?verifyKey=").concat(Config.getInst().authKey)+"&qq="+ Config.getInst().QQ;
                        getLogger().info("[MiraiAdapter] Connecting to {}", url);
                        wsMessageListener=httpClient.newWebSocketBuilder().buildAsync(new URI(url),new WsListener()).join();
                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                });
        if (Cred.sessionKey == null)
            getLogger().warn("Failed to get session. Response: {}", auth.getRawResponse());
        if (Config.getInst().autoAcceptGroupRequest || Config.getInst().autoAcceptFriendRequest)
            MiraiEventBus.register(new AutoAcceptInvite());
        registerCommand(
                LiteralArgumentBuilder.<Source>literal("mirai")
                        .requires(source -> {
                            return source.getSender().hasPermission(Perm.of("mirai.admin"));
                        })
                        .then(LiteralArgumentBuilder.<Source>literal("image")
                                .executes(c -> {
                                    c.getSource().getMessageEvent().reply("[Image:" + Base64.getUrlEncoder().encodeToString("https://i.loli.net/2020/07/11/RuBwdh89AezLHUO.jpg".getBytes()) + "]");
                                    return 0;
                                })
                        )
                        .then(LiteralArgumentBuilder.<Source>literal("at")
                                .executes(c -> {
                                    c.getSource().getMessageEvent().reply("[At:" + c.getSource().getSender().getUniqueID() + "]");
                                            return 0;
                                        }
                                ))
                        .executes(c -> {
                            MessageEvent m = c.getSource().getMessageEvent();
                            m.reply(m.getMessageID(), "MiraiAdapter Running!");
                            return 0;
                        })
                        .then(LiteralArgumentBuilder.<Source>literal("pm")
                                .executes(c -> {
                                    MessageEvent me = c.getSource().getMessageEvent();
                                    Core.get().bot("QQ").get().asContact(me.getUserID()).get().sendMessage("HI~");
                                    return 0;
                                })
                        )

        );
    }

    public synchronized void refreshContacts() {
        QQBot bot = (QQBot) Core.get().bot("QQ").get();
        GroupList.builder().sessionKey(Cred.sessionKey).build().send().asGroups().forEach(this::refreshGroup);
        getLogger().info("[MiraiAdapter] Updating friendList");
        FriendList.builder().sessionKey(Cred.sessionKey).build()
                .send().asGroups().forEach(contact -> {
            QQBot.contactsAndGroup.put(contact.getId(), -1L);
        });
    }

    public synchronized void refreshGroup(MiraiGroup g) {
        QQBot bot = (QQBot) Core.get().bot("QQ").get();
        Set<Contact> contactSet = new HashSet<>();
        //load group members
        GroupMemberList.builder().sessionKey(Cred.sessionKey)
                .target(g.getId())
                .build().send().asList().forEach(ms -> {
            QQContact contact = new QQContact(ms.getId(), ms.getMemberName(), ms.getPermission());
            contactSet.add(contact);
            QQBot.contactsAndGroup.put(contact.getID(), g.getId());
            bot.addContact(contact, true);
        });

        QQGroup group = new QQGroup(g.getId(), contactSet, g.getName());
        bot.addGroup(group, true);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        if (authed) {
            getLogger().info("Logging out..");
            getLogger().info("Releasing session: {}", Release.builder()
                    .qq(Config.getInst().QQ)
                    .sessionKey(Cred.sessionKey)
                    .build()
                    .send()
                    .asMessage().orElse("Unknown Error")
            );
        }
        if (wsMessageListener != null) {
            wsMessageListener.sendClose(1000, "onDisable");
        }
        threadPool.shutdownNow();
    }

    protected void requestReconnect() {
        if (++reconnectCounter >= Config.getInst().reconnectLimit) {
            getLogger().error("Gave up to connect Mirai!!");
            return;
        }
        wsMessageListener.abort();
        try {
            getLogger().info("[Reconnecter] Waiting to reconnect Mirai...");
            Thread.sleep(Config.getInst().reconnectTimeWait);
            reAuth();
        } catch (Exception e) {
            if (!(e instanceof InterruptedException)) {
                reconnectCounter--;
                requestReconnect();
                return;
            }
        }
        getLogger().info("[Reconnecter] Re-connecting to Mirai");
        try {
            httpClient.newWebSocketBuilder().buildAsync(new URI(Config.getInst().baseUrl.replaceAll("http", "ws").concat("message?sessionKey=").concat(Cred.sessionKey)),new WsListener()).thenApply(ez->wsMessageListener=ez);
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    protected synchronized void reAuth() {
        Auth auth = Auth.builder()
                .verifyKey(Config.getInst().authKey)
                .build();
        auth.send()
                .asSession()
                .ifPresent(s -> {
                    authed = true;
                    getLogger().info("[MiraiAdapter] Logged in!");
                    Cred.sessionKey = s;
                    String response = Bind.builder().qq(Config.getInst().QQ)
                            .sessionKey(Cred.sessionKey)
                            .build()
                            .send()
                            .getRawResponse();
                    getLogger().info("[MiraiAdapter] Try verify: {}", response);
                });
    }
}
