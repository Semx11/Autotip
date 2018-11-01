package me.semx11.autotip.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.chat.ChatComponentBuilder;
import me.semx11.autotip.chat.MessageUtil;
import me.semx11.autotip.config.GlobalSettings.GameGroup;
import me.semx11.autotip.gson.exclusion.Exclude;

public abstract class Stats {

    public static final DecimalFormat FORMAT = (DecimalFormat) NumberFormat.getInstance(Locale.US);

    @Exclude
    protected final Autotip autotip;

    protected int tipsSent = 0;
    protected int tipsReceived = 0;
    protected int xpSent = 0;
    protected int xpReceived = 0;

    Map<String, Coins> gameStatistics = new ConcurrentHashMap<>();

    public Stats(Autotip autotip) {
        this.autotip = autotip;
    }

    public String getTipsTotal() {
        return FORMAT.format(this.getTipsTotalInt());
    }

    public int getTipsTotalInt() {
        return tipsSent + tipsReceived;
    }

    public String getTipsSent() {
        return FORMAT.format(this.getTipsSentInt());
    }

    public int getTipsSentInt() {
        return tipsSent;
    }

    public void addTipsSent(int tips) {
        this.tipsSent += tips;
    }

    public String getTipsReceived() {
        return FORMAT.format(this.getTipsReceivedInt());
    }

    public int getTipsReceivedInt() {
        return tipsReceived;
    }

    public void addTipsReceived(int tips) {
        this.tipsReceived += tips;
    }

    public String getXpTotal() {
        return FORMAT.format(this.getXpTotalInt());
    }

    public int getXpTotalInt() {
        return xpSent + xpReceived;
    }

    public String getXpSent() {
        return FORMAT.format(this.getXpSentInt());
    }

    public int getXpSentInt() {
        return xpSent;
    }

    public void addXpSent(int xp) {
        this.xpSent += xp;
    }

    public String getXpReceived() {
        return FORMAT.format(this.getXpReceivedInt());
    }

    public int getXpReceivedInt() {
        return xpReceived;
    }

    public void addXpReceived(int xp) {
        this.xpReceived += xp;
    }

    public Map<String, Coins> getGameStatistics() {
        return gameStatistics;
    }

    public void addCoinsSent(String game, int coins) {
        this.addCoins(game, coins, 0);
    }

    public void addCoinsReceived(String game, int coins) {
        this.addCoins(game, 0, coins);
    }

    public void addCoins(String game, int coinsSent, int coinsReceived) {
        this.addCoins(game, new Coins(coinsSent, coinsReceived));
    }

    protected void addCoins(String game, Coins coins) {
        for (GameGroup group : autotip.getGlobalSettings().getGameGroups()) {
            if (game.equals(group.getName())) {
                for (String groupGame : group.getGames()) {
                    this.addCoins(groupGame, coins);
                }
                return;
            }
        }
        this.gameStatistics.merge(game, coins, Coins::merge);
    }

    public Stats merge(final Stats that) {
        this.tipsSent += that.tipsSent;
        this.tipsReceived += that.tipsReceived;
        this.xpSent += that.xpSent;
        this.xpReceived += that.xpReceived;
        that.gameStatistics.forEach(this::addCoins);
        return this;
    }

    public void print() {
        MessageUtil messageUtil = autotip.getMessageUtil();
        messageUtil.separator();
        gameStatistics.entrySet().stream()
                .sorted(Map.Entry.<String, Coins>comparingByValue().reversed())
                .forEach(entry -> {
                    String game = entry.getKey();
                    Coins coins = entry.getValue();
                    ChatComponentBuilder.of("&a{}: &e{} coins", game, coins.getTotal())
                            .setHover("&a{}\n&cBy sending: &e{} coins\n&9By receiving: &e{} coins",
                                    game, coins.getSent(), coins.getReceived())
                            .send();
                });
        ChatComponentBuilder
                .of("&6Tips: {}", this.getTipsTotal())
                .setHover("&cSent: &6{} tips\n&9Received: &6{} tips",
                        this.getTipsSent(), this.getTipsReceived())
                .send();
        ChatComponentBuilder
                .of("&9XP: {}", this.getXpTotal())
                .setHover("&cBy sending: &9{} XP\n&9By receiving: {} XP",
                        this.getXpSent(), this.getXpReceived())
                .send();
        if (this instanceof StatsDaily) {
            // TODO: Print date(s)
            // TODO: Add above messages to locale
        }
        messageUtil.separator();
    }

}
