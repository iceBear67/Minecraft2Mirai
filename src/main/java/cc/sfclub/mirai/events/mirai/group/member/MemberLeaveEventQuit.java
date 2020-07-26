package cc.sfclub.mirai.events.mirai.group.member;

import cc.sfclub.mirai.packets.received.sender.MiraiGroupSender;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class MemberLeaveEventQuit implements MemberEvent {
    @SerializedName("member")
    private MiraiGroupSender leaved;
}
