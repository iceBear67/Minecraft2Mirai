package cc.sfclub.mirai.packets;

import cc.sfclub.mirai.Packet;
import lombok.Builder;

@Builder
public class DealGroupJoinRequest extends Packet {
    private String sessionKey;
    private long eventId;
    private long fromId;
    private long groupId;
    private int operate;
    private String message;

    @Override
    public String getTargetedPath() {
        return "resp/memberJoinRequestEvent";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    public class Operate {
        public static final int ACCEPT = 0;
        public static final int DENY = 1;
        public static final int IGNORE = 2;
        public static final int DENY_AND_BLOCKLIST = 3;
        public static final int IGNORE_AND_BLOCKLIST = 4;
    }
}
