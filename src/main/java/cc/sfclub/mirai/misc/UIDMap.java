package cc.sfclub.mirai.misc;

import cc.sfclub.core.Core;
import cc.sfclub.mirai.Config;
import cc.sfclub.user.User;
import lombok.Data;
import org.nutz.dao.Cnd;
import org.nutz.dao.entity.annotation.Table;

import java.util.Optional;

@Table("MiraiAdapter_UIDMap")
@Data
public class UIDMap {
    private long QQUIN;
    private String UserID;

    public static Optional<UIDMap> fromQQUIN(long QQUIN) {
        UIDMap uidMap = Core.get().ORM().fetch(UIDMap.class, Cnd.where("QQUIN", "=", QQUIN));
        if (uidMap == null && Config.getInst().autoCreateAccount) {
            User user = new User();
            Core.get().ORM().insert(user);
            uidMap = new UIDMap();
            uidMap.setQQUIN(QQUIN);
            uidMap.setUserID(user.getUniqueID());
            Core.get().ORM().insert(uidMap);
            return Optional.of(uidMap);
        }
        return Optional.ofNullable(Core.get().ORM().fetch(UIDMap.class, Cnd.where("QQUIN", "=", QQUIN)));
    }

    public static Optional<UIDMap> fromUUID(User user) {
        return fromUUID(user.getUniqueID());
    }

    public static Optional<UIDMap> fromUUID(String userID) {
        return Optional.ofNullable(Core.get().ORM().fetch(UIDMap.class, Cnd.where("UserID", "=", userID)));
    }
}
