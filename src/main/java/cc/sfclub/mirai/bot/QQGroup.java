package cc.sfclub.mirai.bot;

import cc.sfclub.mirai.Cred;
import cc.sfclub.mirai.packets.GroupMemberInfo;
import cc.sfclub.mirai.packets.GroupMessages;
import cc.sfclub.mirai.packets.GroupQuoteMessage;
import cc.sfclub.mirai.utils.MessageUtil;
import cc.sfclub.transform.ChatGroup;
import cc.sfclub.transform.Contact;

import java.util.Set;

public class QQGroup extends ChatGroup {
    private String name;

    public QQGroup(long ID, Set<Contact> members, String name) {
        super(ID, members);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String honorOf(Contact contact) {
        return GroupMemberInfo.builder().groupId(getID())
                .memberId(contact.getID())
                .sessionKey(Cred.sessionKey)
                .build()
                .send()
                .asInfo().specialTitle;
    }

    @Override
    public String nickOf(Contact contact) {
        return GroupMemberInfo.builder().groupId(getID())
                .memberId(contact.getID())
                .sessionKey(Cred.sessionKey)
                .build()
                .send()
                .asInfo().groupNick;
    }

    @Override
    public Role roleOf(Contact contact) {
        if (!(contact instanceof QQContact)) return Role.MEMBER;
        QQContact qqc = (QQContact) contact;
        switch (qqc.getRole()) {
            case "MEMBER":
                return Role.MEMBER;
            case "ADMINISTRATOR":
                return Role.ADMIN;
            case "OWNER":
                return Role.OWNER;
        }
        return Role.MEMBER;
    }

    @Override
    public void sendMessage(String s) {
        GroupMessages.builder()
                .sessionKey(Cred.sessionKey)
                .target(this.getID())
                .messageChain(MessageUtil.deserializeCatCodes(s))
                .build()
                .send();
    }

    @Override
    public void reply(long l, String s) {
        GroupQuoteMessage.builder()
                .sessionKey(Cred.sessionKey)
                .target(this.getID())
                .messageChain(MessageUtil.deserializeCatCodes(s))
                .quote(l)
                .build()
                .send();
    }
}
