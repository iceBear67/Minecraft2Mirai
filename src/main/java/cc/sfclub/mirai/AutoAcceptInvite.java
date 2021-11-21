package cc.sfclub.mirai;

import cc.sfclub.mirai.events.mirai.friend.NewFriendRequestEvent;
import cc.sfclub.mirai.events.mirai.group.bot.BotInvitedJoinGroupRequestEvent;
import cc.sfclub.mirai.packets.DealFriendRequest;
import cc.sfclub.mirai.packets.DealGroupInviteRequest;
import org.greenrobot.eventbus.Subscribe;

public class AutoAcceptInvite {
    @Subscribe
    public void on(BotInvitedJoinGroupRequestEvent e) {
        if (!Config.getInst().autoAcceptGroupRequest) return;
        e.deal(DealGroupInviteRequest.Operate.ACCEPT, "Hi");
        AdapterMain.INSTANCE.refreshContacts();
        AdapterMain.INSTANCE.getLogger().info("[MiraiAdapter] AutoAcceptGroupInvitation - Accepted: {} from group: {} (said {})", e.getFromId(), e.getGroupId(), e.getMessage());
    }

    @Subscribe
    public void onFriendRequest(NewFriendRequestEvent e) {
        if (!Config.getInst().autoAcceptFriendRequest) return;
        e.deal(DealFriendRequest.Operate.ACCEPT, "Hi");
        AdapterMain.INSTANCE.refreshContacts();
        AdapterMain.INSTANCE.getLogger().info("[MiraiAdapter] AutoAcceptFriendReq - Accepted: {} from group: {} (said {})", e.getFromId(), e.getGroupId(), e.getMessage());
    }
}
