package me.semx11.autotip.core;

import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.stats.StatsDaily;
import org.apache.commons.io.FileUtils;

public class StatsManager {

    private final Autotip autotip;
    private final Map<LocalDate, StatsDaily> cache = new ConcurrentHashMap<>();

    private StatsDaily statsDaily;
    private AtomicInteger ticks;

    public StatsManager(Autotip autotip) {
        this.autotip = autotip;
        this.ticks = new AtomicInteger(-1);
    }

    public synchronized StatsDaily getToday() {
        LocalDate today = LocalDate.now();
        if (statsDaily == null) {
            statsDaily = this.get(today);
        }
        if (!statsDaily.getDate().equals(today)) {
            this.save(statsDaily);
            statsDaily = this.get(today);
        }
        // Save after 7 seconds (20 ticks/sec) of no access
        this.ticks.set(7 * 20);
        return statsDaily;
    }

    public StatsDaily get(LocalDate date) {
        if (cache.containsKey(date)) {
            return cache.get(date);
        }
        StatsDaily stats = this.load(new StatsDaily(autotip, date));
        cache.put(date, stats);
        return stats;
    }

    public void save(StatsDaily stats) {
        File file = stats.getFile();
        try {
            String json = autotip.getGson().toJson(stats);
            FileUtils.writeStringToFile(file, json, StandardCharsets.UTF_8);
            Autotip.LOGGER.info("Saved " + stats.getFile().getName());
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not write to " + file, e);
        }
    }

    public StatsDaily load(StatsDaily stats) {
        File file = stats.getFile();
        try {
            String json = FileUtils.readFileToString(file);
            return stats.merge(autotip.getGson().fromJson(json, StatsDaily.class));
        } catch (FileNotFoundException e) {
            Autotip.LOGGER.info(file.getName() + " does not exist, creating...");
        } catch (JsonSyntaxException | IllegalArgumentException e) {
            Autotip.LOGGER.warn(file.getName() + " has invalid contents, resetting...");
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not read " + file.getName() + "!", e);
        }
        this.save(stats);
        return stats;
    }

    public void saveCycle() {
        if (ticks.get() > 0) {
            ticks.decrementAndGet();
            return;
        }
        if (ticks.get() == 0) {
            this.save(statsDaily);
            ticks.decrementAndGet();
        }
    }

}
