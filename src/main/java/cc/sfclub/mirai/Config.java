package cc.sfclub.mirai;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class Config {
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private static Config inst;
    public String baseUrl = "http://localhost:8080/";
    public long QQ = 3325273573L;
    public String authKey = "AuthKey_HERE";
    public boolean displayMessage = false;
    public int responseWaitTime = 10;
    public int reconnectLimit = 10;
    public int reconnectTimeWait = 3000;
    public boolean usePrefixFromMC=true;
    public boolean usePrefixFromQQ=true;
    /***/
    public boolean debug=false;
    public long targetGroup=114514L;
}
