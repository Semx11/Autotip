package me.semx11.autotip.stats;

import java.time.LocalDate;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.util.FileUtil;

public class StatsManager {

    private LocalDate today;
    private DailyStatistic dailyStatistic;

    public StatsManager() {
        this.today = LocalDate.now();
        this.dailyStatistic = new DailyStatistic(today).load();
    }

    public synchronized DailyStatistic getToday() {
        this.today = LocalDate.now();
        if (!dailyStatistic.getDate().equals(today)) {
            this.dailyStatistic.save();
            this.dailyStatistic = new DailyStatistic(today).load();
        }
        return dailyStatistic;
    }

}
