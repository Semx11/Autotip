package me.semx11.autotip.universal;

import me.semx11.autotip.util.StringUtil;

/**
 * Temporary solution until I fix all reflection related spaghetti
 */
public class ChatComponentBuilder {

    private static final String PREFIX = "&6A&eT &8> &7";

    private String text;
    private String hoverText;
    private String url;

    private ChatComponentBuilder(String text, Object... params) {
        this.text = StringUtil.params(PREFIX + text, params);
    }

    public static ChatComponentBuilder of(String text, Object... params) {
        return new ChatComponentBuilder(text, params);
    }

    public ChatComponentBuilder setUrl(String url, Object... params) {
        this.url = StringUtil.params(url, false, params);
        return this;
    }

    public ChatComponentBuilder setHoverText(String hoverText, Object... params) {
        this.hoverText = StringUtil.params(hoverText, params);
        return this;
    }

    public void send() {
        UniversalUtil.addChatMessage(text, url, hoverText);
    }

}
