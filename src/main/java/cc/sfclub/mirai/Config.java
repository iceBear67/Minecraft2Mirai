package cc.sfclub.mirai;

import cc.sfclub.util.common.JsonConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class Config extends JsonConfig {
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private static Config inst;
    public String baseUrl="http://localhost:8080/";
    public long QQ=0L;
    public String authKey="AuthKey_HERE";
    public boolean displayMessage=false;
    public Config(String rootDir) {
        super(rootDir);
    }
}
