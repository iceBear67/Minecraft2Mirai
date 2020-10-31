package cc.sfclub.mirai;

import cc.sfclub.mirai.events.mirai.group.bot.BotInvitedJoinGroupRequestEvent;
import cc.sfclub.mirai.packets.DealGroupInviteRequest;
import org.greenrobot.eventbus.Subscribe;

public class AutoAcceptInvite {
    @Subscribe
    public void on(BotInvitedJoinGroupRequestEvent e) {
        e.deal(DealGroupInviteRequest.Operate.ACCEPT, "Hi");
        AdapterMain.get(AdapterMain.class).getLogger().info("[MiraiAdapter] AutoAcceptGroupInvitation - Accepted: {} from group: {} (said {})", e.getFromId(), e.getGroupId(), e.getMessage());
    }
}
