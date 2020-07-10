package cc.sfclub.mirai.bot;

import cc.sfclub.mirai.misc.UIDMap;
import cc.sfclub.transform.Bot;
import cc.sfclub.transform.Contact;

import java.util.Optional;

public class QQBot extends Bot {
    @Override
    public String getName() {
        return "QQ";
    }

    @Override
    public Optional<Contact> asContact(String userId) {
        return getContact(UIDMap.fromUUID(userId).orElseThrow(NullPointerException::new).getQQUIN());
    }
}
