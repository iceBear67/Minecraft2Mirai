package cc.sfclub.mirai.adapts;

public interface Replyable {
    void reply(String message);
    boolean hasPerm(String perm);
}
