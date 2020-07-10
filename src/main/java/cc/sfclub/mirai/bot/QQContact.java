package cc.sfclub.mirai.bot;

import cc.sfclub.core.Core;
import cc.sfclub.mirai.misc.UIDMap;
import cc.sfclub.transform.Contact;
import cc.sfclub.user.User;
import lombok.Getter;
import org.nutz.dao.Cnd;

public class QQContact extends Contact {
    private String nick;
    @Getter
    private String role;

    public QQContact(long ID, String nick, String role) {
        super(ID);
        this.nick = nick;
        this.role = role;
    }

    @Override
    public String getNickname() {
        return nick;
    }

    @Override
    public String getUsername() {
        return nick;
    }

    @Override
    public User asPermObj() {
        String userID = UIDMap.fromQQUIN(this.getID()).orElseThrow(() -> new IllegalArgumentException("Unknown error")).getUserID();
        return Core.get().ORM().fetch(User.class, Cnd.where("UniqueID", "=", userID));
    }

    @Override
    public void sendMessage(String s) {

    }

    @Override
    public void reply(long l, String s) {

    }
}
