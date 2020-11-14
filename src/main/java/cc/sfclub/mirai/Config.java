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
    /**
     * 如果设置为false,则拒收未注册用户的信息。
     */
    public boolean autoCreateAccount = true;
    public boolean autoAcceptFriendRequest = true;
    public boolean autoAcceptGroupRequest = true;
}
