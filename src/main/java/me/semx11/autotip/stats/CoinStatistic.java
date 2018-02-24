package me.semx11.autotip.stats;

public class CoinStatistic {

    private int coinsBySending;
    private int coinsByReceiving;

    public CoinStatistic(int coinsBySending, int coinsByReceiving) {
        this.coinsBySending = coinsBySending;
        this.coinsByReceiving = coinsByReceiving;
    }

    public int getSent() {
        return coinsBySending;
    }

    public void addSent(int coins) {
        coinsBySending += coins;
    }

    public int getReceived() {
        return coinsByReceiving;
    }

    public void addReceived(int coins) {
        coinsByReceiving += coins;
    }

    public CoinStatistic merge(final CoinStatistic that) {
        this.coinsBySending += that.coinsBySending;
        this.coinsByReceiving += that.coinsByReceiving;
        return this;
    }

}
