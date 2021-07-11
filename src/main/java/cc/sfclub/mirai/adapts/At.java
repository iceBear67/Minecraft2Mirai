package cc.sfclub.mirai.adapts;

import lombok.Builder;

@Builder
public class At {

    /**
     */
    public String userId;
    /**
     */
    public boolean all;

    @Override
    public String toString() {
        if (all) {
            return "[AtAll]";
        }
        return "[At:" + userId + "]";
    }
}
