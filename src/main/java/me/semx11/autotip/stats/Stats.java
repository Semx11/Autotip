package me.semx11.autotip.stats;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.config.GlobalSettings.GameGroup;
import me.semx11.autotip.gson.exclusion.Exclude;

public abstract class Stats {

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

    public int getTipsSent() {
        return tipsSent;
    }

    public void addTipsSent(int tips) {
        this.tipsSent += tips;
    }

    public int getTipsReceived() {
        return tipsReceived;
    }

    public void addTipsReceived(int tips) {
        this.tipsReceived += tips;
    }

    public int getXpSent() {
        return xpSent;
    }

    public void addXpSent(int xp) {
        this.xpSent += xp;
    }

    public int getXpReceived() {
        return xpReceived;
    }

    public void addXpReceived(int xp) {
        this.xpReceived += xp;
    }

    public Map<String, Coins> getGameStatistics() {
        return gameStatistics;
    }

    public void addCoinsSent(String gameName, int coinsSent) {
        this.addCoins(gameName, coinsSent, 0);
    }

    public void addCoinsReceived(String gameName, int coinsReceived) {
        this.addCoins(gameName, 0, coinsReceived);
    }

    public void addCoins(String gameName, int coinsSent, int coinsReceived) {
        this.addCoins(gameName, new Coins(coinsSent, coinsReceived));
    }

    protected void addCoins(String gameName, Coins coins) {
        for (GameGroup group : autotip.getGlobalSettings().getGameGroups()) {
            if (gameName.equals(group.getName())) {
                for (String game : group.getGames()) {
                    this.addCoins(game, coins);
                }
            }
        }
        this.gameStatistics.merge(gameName, coins, Coins::merge);
    }

    public Stats merge(final Stats that) {
        this.tipsSent += that.tipsSent;
        this.tipsReceived += that.tipsReceived;
        this.xpSent += that.xpSent;
        this.xpReceived += that.xpReceived;
        that.gameStatistics.forEach(this::addCoins);
        return this;
    }

}
