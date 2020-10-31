package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;

@Builder
public class GroupMemberInfo extends Packet {
    private String sessionKey;
    @SerializedName("target")
    private long groupId;
    private long memberId;

    @Override
    public String getTargetedPath() {
        return "memberInfo?sessionKey=" + sessionKey + "&target=" + groupId + "&memberId=" + memberId;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public GroupMemberInfo send() {
        super.send();
        return this;
    }

    public Resp asInfo() {
        return gson.fromJson(getRawResponse(), Resp.class);
    }

    public class Resp {
        @SerializedName("name")
        public String groupNick;
        public String specialTitle;
    }
}
