package cc.sfclub.mirai.bot;

import cc.sfclub.core.Core;
import cc.sfclub.mirai.Cred;
import cc.sfclub.mirai.packets.FriendMessage;
import cc.sfclub.mirai.packets.FriendQuoteMessage;
import cc.sfclub.mirai.packets.TempMessage;
import cc.sfclub.mirai.packets.TempQuoteMessage;
import cc.sfclub.mirai.utils.MessageUtil;
import cc.sfclub.transform.Contact;
import cc.sfclub.user.User;
import lombok.Getter;

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

    public boolean isTemp() {
        return QQBot.contactsAndGroup.get(this.getID()) != -1;
    }

    @Override
    public String getUsername() {
        return nick;
    }

    @Override
    public User asPermObj() {
        return Core.get().userManager().byPlatformID(QQBot.PLATFORM_NAME, String.valueOf(getID()));
    }

    @Override
    public void sendMessage(String s) {
        if (isTemp())
            TempMessage.builder().messageChain(MessageUtil.deserializeCatCodes(s)).sessionKey(Cred.sessionKey).target(super.getID()).group(QQBot.contactsAndGroup.get(super.getID())).build().send();
        else {
            FriendMessage.builder().messageChain(MessageUtil.deserializeCatCodes(s)).sessionKey(Cred.sessionKey).target(super.getID()).build().send();
        }
    }

    @Override
    public void reply(long l, String s) {
        if (isTemp())
            TempQuoteMessage.builder().messageChain(MessageUtil.deserializeCatCodes(s)).sessionKey(Cred.sessionKey).quote((int) l).target(super.getID()).group(QQBot.contactsAndGroup.get(super.getID())).build().send();
        else {
            FriendQuoteMessage.builder().messageChain(MessageUtil.deserializeCatCodes(s)).sessionKey(Cred.sessionKey).quote((int) l).target(super.getID()).build().send();
        }
    }
}
