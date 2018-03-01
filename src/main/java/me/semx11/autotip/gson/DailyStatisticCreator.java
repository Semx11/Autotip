package me.semx11.autotip.gson;

import com.google.gson.InstanceCreator;
import java.lang.reflect.Type;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.stats.DailyStatistic;

public class DailyStatisticCreator implements InstanceCreator<DailyStatistic> {

    private final Autotip autotip;

    public DailyStatisticCreator(Autotip autotip) {
        this.autotip = autotip;
    }

    @Override
    public DailyStatistic createInstance(Type type) {
        return new DailyStatistic(autotip);
    }

}
