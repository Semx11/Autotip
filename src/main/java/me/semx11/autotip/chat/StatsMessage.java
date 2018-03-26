package me.semx11.autotip.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import me.semx11.autotip.gson.exclusion.Exclude;

public class StatsMessage extends Message {

    @Exclude
    private final Map<String, StatsMessageMatcher> statsMessageCache = new ConcurrentHashMap<>();

    private StatsType statsType;

    public StatsMessage() {
        super();
    }

    public StatsMessage(Pattern pattern, MessageOption hideFor, StatsType statsType) {
        super(pattern, hideFor);
        this.statsType = statsType;
    }

    public StatsMessageMatcher getMatcherFor(String input) {
        if (statsMessageCache.containsKey(input)) {
            return statsMessageCache.get(input);
        }
        StatsMessageMatcher matcher = new StatsMessageMatcher(pattern, input, statsType);
        statsMessageCache.put(input, matcher);
        return matcher;
    }

}
