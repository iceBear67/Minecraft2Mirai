package cc.sfclub.mirai.packets.received.message.types;

import cc.sfclub.mirai.packets.received.message.MiraiTypeMessage;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Poke extends MiraiTypeMessage {
    //QQ Pokes https://github.com/project-mirai/mirai-api-http/blob/master/MessageType.md
    public static final String SHAKE = "Poke";
    public static final String SHOW_LOVE = "ShowLove";
    public static final String LIKE = "Like";
    public static final String HEART_BROKEN = "Heartbroken";
    public static final String SIX_SIX_SIX = "SixSixSix";
    public static final String FANG_DA_ZHAO = "FangDaZhao";

    private String name;
}
