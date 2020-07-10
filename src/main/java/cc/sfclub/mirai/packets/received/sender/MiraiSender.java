package cc.sfclub.mirai.packets.received.sender;

import lombok.Getter;

@Getter
public class MiraiSender {
    private long id;
    private String memberName;
    private String permission;
    private MiraiGroup group;
}