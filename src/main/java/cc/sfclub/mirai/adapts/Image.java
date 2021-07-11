package cc.sfclub.mirai.adapts;

import lombok.Builder;

/**
 * 图片消息
 */
@Builder
public class Image {
    /**
     */
    public String URI;
    /**
     */
    public String ID;

    @Override
    public String toString() {
        if (URI.isEmpty()) {
            return "[Image:" + ID + "]";
        }
        return "[Image:" + Base64.URLSafe.encode(URI) + "]";
    }
}
