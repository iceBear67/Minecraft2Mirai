package cc.sfclub.mirai.events.mirai.group;

import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class MemberLeaveEventKick extends GroupEvent {
    @SerializedName("member")
    private MiraiSender kicked;
}
