package me.semx11.autotip.config;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import me.semx11.autotip.chat.Message;
import me.semx11.autotip.chat.StatsMessage;

public class GlobalSettings {

    private String hypixelHeader;
    private int xpPerTipReceived;
    private LocalDate xpChangeDate;
    private List<GameGroup> gameGroups;
    private List<GameAlias> gameAliases;
    private List<Message> messages;
    private List<StatsMessage> statsMessages;

    public String getHypixelHeader() {
        return hypixelHeader;
    }

    public int getXpPerTipReceived() {
        return xpPerTipReceived;
    }

    public LocalDate getXpChangeDate() {
        return xpChangeDate;
    }

    public List<GameGroup> getGameGroups() {
        return gameGroups;
    }

    public List<GameAlias> getGameAliases() {
        return gameAliases;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<StatsMessage> getStatsMessages() {
        return statsMessages;
    }

    public static class GameGroup {

        private String name;
        private Set<String> games;

        public String getName() {
            return name;
        }

        public Set<String> getGames() {
            return games;
        }

    }

    public static class GameAlias {

        private String alias;
        private List<String> aliases;

        private String game;
        private List<String> games;

        public List<String> getAliases() {
            if (alias != null) {
                return Collections.singletonList(alias);
            }
            return aliases;
        }

        public List<String> getGames() {
            if (game != null) {
                return Collections.singletonList(game);
            }
            return games;
        }

    }

}
