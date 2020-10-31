package cc.sfclub.mirai;

import cc.sfclub.mirai.events.mirai.friend.NewFriendRequestEvent;
import cc.sfclub.mirai.packets.DealFriendRequest;
import org.greenrobot.eventbus.Subscribe;

public class AutoAcceptFriendReq {
    @Subscribe
    public void on(NewFriendRequestEvent e) {
        e.deal(DealFriendRequest.Operate.ACCEPT, "Hi");
        AdapterMain.get(AdapterMain.class).getLogger().info("[MiraiAdapter] AutoAcceptFriendReq - Accepted: {} from group: {} (said {})", e.getFromId(), e.getGroupId(), e.getMessage());
    }
}
