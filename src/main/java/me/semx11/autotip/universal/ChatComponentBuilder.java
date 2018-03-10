package me.semx11.autotip.universal;

/**
 * Temporary solution until I fix all reflection related spaghetti
 */
public class ChatComponentBuilder {

    private String text;
    private String hoverText;
    private String url;

    private ChatComponentBuilder(String text) {
        this.text = text;
    }

    public ChatComponentBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public ChatComponentBuilder setHoverText(String hoverText) {
        this.hoverText = hoverText;
        return this;
    }

    public void send() {
        UniversalUtil.addChatMessage(text, url, hoverText);
    }

    public static ChatComponentBuilder of(String text) {
        return new ChatComponentBuilder(text);
    }

}
