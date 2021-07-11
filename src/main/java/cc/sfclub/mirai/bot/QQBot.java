package cc.sfclub.mirai.bot;

import cc.sfclub.mirai.adapts.Bot;
import cc.sfclub.mirai.adapts.Contact;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QQBot extends Bot {
    public static Map<Long, Long> contactsAndGroup = new HashMap<>();
    public static final String PLATFORM_NAME = "QQ";
    @Override
    public String getName() {
        return PLATFORM_NAME;
    }

    @Override
    public Optional<Contact> asContact(String userId) {
        return null;
    }
}
