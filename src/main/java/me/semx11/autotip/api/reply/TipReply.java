package me.semx11.autotip.api.reply;

import java.util.List;
import me.semx11.autotip.api.util.RequestType;

public class TipReply extends AbstractReply {

    private List<Tip> tips;

    public TipReply() {
    }

    public TipReply(boolean success) {
        super(success);
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

        public String getGamemode() {
            return gamemode;
        }

        public boolean hasUsername() {
            return username != null;
        }

        public String getUsername() {
            return username;
        }

    }

}
