package cc.sfclub.mirai.adapts;

import cc.sfclub.mirai.AdapterMain;
import cc.sfclub.mirai.Config;
import cc.sfclub.mirai.packets.received.message.group.MiraiGroupMessage;
import cc.sfclub.mirai.utils.MessageUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

public class BukkitMessageEvent implements Listener {

    private final HoverEvent COMPILED_HOVER = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(ChatColor.WHITE + "这条信息来自 QQ")));

    @Subscribe
    public void onMiraiMessage(MiraiGroupMessage miraiGroupMessage){
        if(miraiGroupMessage.getSender().getGroup().getId()!=Config.getInst().targetGroup){
            return;
        }
        if(MessageUtil.deserializeChain(miraiGroupMessage.getMessageChain()).startsWith(".list")){
            AdapterMain.getPlugin(AdapterMain.class).getBot().getGroup(Config.getInst().targetGroup).get()
                    .sendMessage("Online players: "+Arrays.toString(Bukkit.getServer().getOnlinePlayers().stream().map(HumanEntity::getName).toArray()));
            return;
        }
        String context = MessageUtil.deserializeChain(miraiGroupMessage.getMessageChain());
        if(Config.getInst().usePrefixFromQQ){
            if(context.startsWith("#")){
                String sender = miraiGroupMessage.getSender().getMemberName() ;
                var bcs = TextComponent.fromLegacyText(ChatColor.GRAY+sender+ChatColor.WHITE+" > "+context.replaceFirst("#",""));
                for (BaseComponent bc : bcs) {
                    bc.setHoverEvent(COMPILED_HOVER);
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.spigot().sendMessage(bcs);
                }
            }
        }else{
            String sender = miraiGroupMessage.getSender().getMemberName() ;
            var bcs = TextComponent.fromLegacyText(ChatColor.GRAY+sender+ChatColor.WHITE+" > "+context);
            for (BaseComponent bc : bcs) {
                bc.setHoverEvent(COMPILED_HOVER);
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.spigot().sendMessage(bcs);
            }
        }
    }
    @EventHandler
    public void onMessage(AsyncPlayerChatEvent chatEvent){
        if(chatEvent.isCancelled()){
            return;
        }
        String sender = chatEvent.getPlayer().getName();
        String message = chatEvent.getMessage();
        if(Config.getInst().usePrefixFromMC){
            if(message.startsWith("#")){
                AdapterMain.getPlugin(AdapterMain.class).getBot().getGroup(Config.getInst().targetGroup).orElseThrow(AssertionError::new)
                        .sendMessage("[MC] "+sender+": "+message.replaceFirst("#",""));
            }
        }else{
            AdapterMain.getPlugin(AdapterMain.class).getBot().getGroup(Config.getInst().targetGroup).orElseThrow(AssertionError::new)
                    .sendMessage("[MC] "+sender+": "+message);

        }

    }
}
