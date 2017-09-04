package me.semx11.autotip.api.reply;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import me.semx11.autotip.api.util.RequestType;

public class TipReply extends AbstractReply {

    private List<Tip> tips;

    public TipReply() {
    }

    public TipReply(boolean success) {
        super(success);
    }

    private TipReply(List<Tip> tips) {
        this.tips = tips;
    }

    public static TipReply getDefault() {
        return new TipReply(Collections.singletonList(new Tip("all", "")));
    }

    public List<Tip> getTips() {
        return tips;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.TIP;
    }

    public static class Tip {

        private String gamemode;
        private String username;

        private Tip() {
        }

        private Tip(String gamemode, String username) {
            this.gamemode = gamemode;
            this.username = username;
        }

        public String getGamemode() {
            return gamemode;
        }

        public String getUsername() {
            return username != null ? username : "";
        }

        public String getAsCommand() {
            return "/tip " + (!Objects.equals(username, "") && username != null
                    ? username + " " : "") + gamemode;
        }

        @Override
        public String toString() {
            return getUsername() + " " + getGamemode();
        }

    }

}
