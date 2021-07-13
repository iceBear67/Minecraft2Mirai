package cc.sfclub.mirai.adapts;

import cc.sfclub.mirai.AdapterMain;
import cc.sfclub.mirai.Config;
import cc.sfclub.mirai.packets.received.message.group.MiraiGroupMessage;
import cc.sfclub.mirai.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

public class BukkitMessageEvent implements Listener {
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
        if(Config.getInst().usePrefix){
            if(context.startsWith("#")){
                String sender = miraiGroupMessage.getSender().getMemberName() +"("+miraiGroupMessage.getSender().getId()+")";
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE+"[QQ] "+ChatColor.GREEN+sender+ChatColor.WHITE+": "+context);
            }
        }else{
            String sender = miraiGroupMessage.getSender().getMemberName() +"("+miraiGroupMessage.getSender().getId()+")";
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE+"[QQ] "+ChatColor.GREEN+sender+ChatColor.WHITE+": "+context);
        }
    }
    @EventHandler
    public void onMessage(AsyncPlayerChatEvent chatEvent){
        if(chatEvent.isCancelled()){
            return;
        }
        String sender = chatEvent.getPlayer().getName();
        String message = chatEvent.getMessage();
        if(Config.getInst().usePrefix){
            if(message.startsWith("#")){
                AdapterMain.getPlugin(AdapterMain.class).getBot().getGroup(Config.getInst().targetGroup).orElseThrow(AssertionError::new)
                        .sendMessage("[MC] "+sender+": "+message);
            }
        }else{
            AdapterMain.getPlugin(AdapterMain.class).getBot().getGroup(Config.getInst().targetGroup).orElseThrow(AssertionError::new)
                    .sendMessage("[MC] "+sender+": "+message);

        }

    }
}
