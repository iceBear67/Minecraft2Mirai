package cc.sfclub.mirai.events.mirai.group.member;

import cc.sfclub.mirai.packets.received.sender.MiraiSender;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class MemberLeaveEventKick implements MemberEvent {
    @SerializedName("member")
    private MiraiSender kicked;
    private MiraiSender operator;
}