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
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import lombok.SneakyThrows;
import org.greenrobot.eventbus.EventBus;

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

public class AdapterMain extends JavaPlugin {
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
    public static AdapterMain INSTANCE;
    //Adaptions below..
    private SimpleConfig<Config> config;
    @Getter
    private Bot bot;
    @Getter
    private CommandDispatcher<Source> dispatcher = new CommandDispatcher<>();

    //@Subscribe
    public Config getMainConfig(){
        return config.get();
    }
    @SuppressWarnings("all")
    public void onServerStart() throws URISyntaxException {
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
                    getLogger().info("[MiraiAdapter] Try verify: "+response);
                    //load groups
                    //Core.get().registerBot(new QQBot());
                    //Bot bot = Core.get().bot(QQBot.PLATFORM_NAME).orElseThrow(() -> new IllegalArgumentException("Unknown error"));

                    bot = new QQBot();
                    Bukkit.getScheduler().runTaskTimerAsynchronously(this,()->{
                        refreshContacts();
                    },0L,30*20L);
                    try {
                        var url = Config.getInst().baseUrl.replaceAll("http", "ws").concat("all?verifyKey=").concat(Config.getInst().authKey)+"&qq="+ Config.getInst().QQ;
                        getLogger().info("[MiraiAdapter] Connecting to "+ url);
                        wsMessageListener=httpClient.newWebSocketBuilder().buildAsync(new URI(url),new WsListener()).join();
                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                    }
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
       // getLogger().info("[MiraiAdapter] Updating friendList");
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

    @SneakyThrows
    @Override
    public void onEnable() {
        INSTANCE = this;
        Bukkit.getScheduler().runTaskAsynchronously(this,()->{
            try {
                onServerStart();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }); // Try to make sure that we're latest to load avoiding some unknown bugs such as token expired?
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
            wsMessageListener.sendClose(1000, "onDisable");
        }
        threadPool.shutdownNow();
    }

    protected void requestReconnect() {
        if (++reconnectCounter >= Config.getInst().reconnectLimit) {
            getLogger().warning("Giving up to connect Mirai!!");
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
            var url = Config.getInst().baseUrl.replaceAll("http", "ws").concat("all?verifyKey=").concat(Config.getInst().authKey)+"&qq="+ Config.getInst().QQ;
            getLogger().info("[MiraiAdapter] Connecting to "+ url);
            wsMessageListener=httpClient.newWebSocketBuilder().buildAsync(new URI(url),new WsListener()).join();
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
                    getLogger().info("[MiraiAdapter] Try verify: "+ response);
                });
    }
}
