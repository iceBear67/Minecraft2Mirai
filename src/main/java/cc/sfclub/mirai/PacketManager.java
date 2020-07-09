package cc.sfclub.mirai;

import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class PacketManager {
    private static final Map<String,Packet> reuse=new HashMap<>();
    private PacketManager(){ }
    @SneakyThrows
    public static <T extends Packet> T build(Class<T> packetType){
        if(reuse.containsKey(packetType.getCanonicalName())){
            return packetType.cast(reuse.get(packetType.getCanonicalName()));
        }
        return packetType.newInstance();
    }
}
