package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import lombok.Builder;

@Builder
public class DealGroupInviteRequest extends Packet {
    private String sessionKey;
    private long eventId;
    private long fromId;
    private long groupId;
    private int operate;
    private String message;

    @Override
    public String getTargetedPath() {
        return "resp/botInvitedJoinGroupRequestEvent";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    public class Operate {
        public static final int ACCEPT = 0;
        public static final int DENY = 1;
    }
}
