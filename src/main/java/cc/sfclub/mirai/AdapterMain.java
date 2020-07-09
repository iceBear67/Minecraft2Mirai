package cc.sfclub.mirai;

import cc.sfclub.core.Core;
import cc.sfclub.events.server.ServerStartedEvent;
import cc.sfclub.events.server.ServerStoppingEvent;
import cc.sfclub.mirai.packets.Auth;
import cc.sfclub.mirai.packets.Release;
import cc.sfclub.plugin.Plugin;
import lombok.Getter;
import okhttp3.OkHttpClient;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class AdapterMain extends Plugin{
    @Getter
    private static final OkHttpClient httpClient=new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
    @Subscribe
    public void onServerStart(ServerStartedEvent e){
        Core.getLogger().info("Mirai-Adapter loading");
        Config conf=new Config(getDataFolder().toString());
        Config.setInst((Config)conf.saveDefaultOrLoad());
        Core.getLogger().info("Try logging in..");
        Auth auth = PacketManager.build(Auth.class);
                auth.send()
                .asSession()
                .ifPresent(s-> {
                    Core.getLogger().info("Logged in! Session: {}",s);
                    Cred.sessionKey=s;
                });
        if(Cred.sessionKey==null)Core.getLogger().warn("Failed to get session. Response: {}",auth.getRawResponse());
    }
    @Subscribe
    public void onServerStop(ServerStoppingEvent e){
        Core.getLogger().info("Logging out..");
        Core.getLogger().info("Releasing session: {}",PacketManager.build(Release.class)
                .sessionKey(Cred.sessionKey)
                .send()
                .getRawResponse());
    }
}
