package cc.sfclub.mirai.packets.received.sender;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MiraiGroup {
    private long id;
    private String name;
    private String permission;
}