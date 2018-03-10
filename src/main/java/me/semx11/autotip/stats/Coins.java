package me.semx11.autotip.stats;

public class Coins {

    private int coinsBySending;
    private int coinsByReceiving;

    public Coins(int coinsBySending, int coinsByReceiving) {
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

    public Coins merge(final Coins that) {
        this.coinsBySending += that.coinsBySending;
        this.coinsByReceiving += that.coinsByReceiving;
        return this;
    }

}
