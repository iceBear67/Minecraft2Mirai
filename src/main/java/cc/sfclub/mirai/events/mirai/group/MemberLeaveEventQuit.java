package cc.sfclub.mirai.events.mirai.group;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class MemberLeaveEventQuit extends GroupEvent {
    @SerializedName("member")
    private boolean leaved;
}
