package cc.sfclub.mirai;

import cc.sfclub.mirai.adapts.*;
import cc.sfclub.mirai.bot.QQBot;
import cc.sfclub.mirai.bot.QQContact;
import cc.sfclub.mirai.bot.QQGroup;
import cc.sfclub.mirai.packets.*;
import cc.sfclub.mirai.packets.received.sender.MiraiGroup;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AdapterMain extends JavaPlugin {
    @Getter
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
    @Getter
    private static final EventBus MiraiEventBus = EventBus.builder().sendNoSubscriberEvent(false).logNoSubscriberMessages(false).build();
    @Getter
    private WebSocket wsMessageListener;
    @Getter
    private ExecutorService threadPool = Executors.newFixedThreadPool(4);
    protected int reconnectCounter = 0;
    protected boolean authed = false;
    //Adaptions below..
    private SimpleConfig<Config> config;
    @Getter
    private Bot bot;
    @Getter
    private CommandDispatcher<Source> dispatcher = new CommandDispatcher<>();

    public Config getMainConfig(){
        return config.get();
    }
    @SuppressWarnings("all")
    public void onServerStart() {
        BukkitMessageEvent a =new BukkitMessageEvent();
        this.getServer().getPluginManager().registerEvents(a,this);
        getMiraiEventBus().register(a);
        getLogger().info("Mirai-Adapter loading");
        config=new SimpleConfig<>(this,Config.class);
        config.saveDefault();
        config.reloadConfig();
        Config.setInst(config.get());
        if (Config.getInst().authKey.equals("AuthKey_HERE")) {
            this.setEnabled(false);
            getLogger().warning("Configure your configurations.");
            return;
        }
        getLogger().info("Try logging in..");
        //Init database
        Auth auth = Auth.builder()
                .authKey(Config.getInst().authKey)
                .build();
        auth.send()
                .asSession()
                .ifPresent(s -> {
                    getLogger().info("[MiraiAdapter] Logged in!");
                    authed = true;
                    Cred.sessionKey = s;
                    String response = Verify.builder().qq(Config.getInst().QQ)
                            .sessionKey(Cred.sessionKey)
                            .build()
                            .send()
                            .getRawResponse();
                    getLogger().info("[MiraiAdapter] Try verify: "+response);
                    //load groups
                    //Core.get().registerBot(new QQBot());
                    //Bot bot = Core.get().bot(QQBot.PLATFORM_NAME).orElseThrow(() -> new IllegalArgumentException("Unknown error"));
                    bot = new QQBot();
                    refreshContacts();
                    Bukkit.getScheduler().runTaskTimerAsynchronously(this,()->{
                        refreshContacts();
                    },0L,30*20L);
                    Request request = new Request.Builder()
                            .url(Config.getInst().baseUrl.replaceAll("http", "ws").concat("message?sessionKey=").concat(Cred.sessionKey))
                            .addHeader("Sec-Websocket-Key", UUID.randomUUID().toString())
                            .build();
                    getLogger().info("[MiraiAdapter] Connecting to "+ Config.getInst().baseUrl.replaceAll("http", "ws"));
                    wsMessageListener = httpClient.newWebSocket(request, new WsListener());
                });
        if (Cred.sessionKey == null)
            getLogger().warning("Failed to get session. Response: "+ auth.getRawResponse());
        dispatcher.register(
                LiteralArgumentBuilder.<Source>literal("mirai")
                .executes(s->{
                    s.getSource().getSender().reply("MiraiAdapter on Bukkit Running!");return 0;})
        );
    }

    public synchronized void refreshContacts() {
        GroupList.builder().sessionKey(Cred.sessionKey).build().send().asGroups().forEach(this::refreshGroup);
        getLogger().info("[MiraiAdapter] Updating friendList");
        FriendList.builder().sessionKey(Cred.sessionKey).build()
                .send().asGroups().forEach(contact -> {
            QQBot.contactsAndGroup.put(contact.getId(), -1L);
        });
    }

    public synchronized void refreshGroup(MiraiGroup g) {
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
        onServerStart();
    }

    @Override
    public void onDisable() {
        if (authed) {
            getLogger().info("Logging out..");
            getLogger().info("Releasing session: {}"+ Release.builder()
                    .qq(Config.getInst().QQ)
                    .sessionKey(Cred.sessionKey)
                    .build()
                    .send()
                    .asMessage().orElse("Unknown Error")
            );
        }
        if (wsMessageListener != null) {
            wsMessageListener.close(1000, "onDisable");
        }
        threadPool.shutdownNow();
    }

    protected void requestReconnect() {
        if (++reconnectCounter >= Config.getInst().reconnectLimit) {
            getLogger().warning("Giving up to connect Mirai!!");
            return;
        }
        wsMessageListener.cancel();
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
        Request request = new Request.Builder()
                .url(Config.getInst().baseUrl.replaceAll("http", "ws").concat("message?sessionKey=").concat(Cred.sessionKey))
                .addHeader("Sec-Websocket-Key", UUID.randomUUID().toString())
                .build();
        wsMessageListener = httpClient.newWebSocket(request, new WsListener());
    }

    protected synchronized void reAuth() {
        Auth auth = Auth.builder()
                .authKey(Config.getInst().authKey)
                .build();
        auth.send()
                .asSession()
                .ifPresent(s -> {
                    authed = true;
                    getLogger().info("[MiraiAdapter] Logged in!");
                    Cred.sessionKey = s;
                    String response = Verify.builder().qq(Config.getInst().QQ)
                            .sessionKey(Cred.sessionKey)
                            .build()
                            .send()
                            .getRawResponse();
                    getLogger().info("[MiraiAdapter] Try verify: "+ response);
                });
    }
}
