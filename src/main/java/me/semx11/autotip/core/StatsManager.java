package me.semx11.autotip.core;

import java.time.LocalDate;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.stats.DailyStatistic;

public class StatsManager {

    private final Autotip autotip;
    private DailyStatistic dailyStatistic;

    public StatsManager(Autotip autotip) {
        this.autotip = autotip;
    }

    public synchronized DailyStatistic getToday() {
        LocalDate today = LocalDate.now();
        if (dailyStatistic == null) {
            dailyStatistic = this.getByDate(today);
        }
        if (!dailyStatistic.getDate().equals(today)) {
            dailyStatistic.save();
            dailyStatistic = this.getByDate(today);
        }
        return dailyStatistic;
    }

    private DailyStatistic getByDate(LocalDate date) {
        return new DailyStatistic(autotip, date).load();
    }

}
