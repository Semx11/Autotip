package me.semx11.autotip.core;

import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.stats.StatsDaily;
import me.semx11.autotip.stats.StatsRange;
import org.apache.commons.io.FileUtils;

public class StatsManager {

    private final Autotip autotip;
    private final Map<LocalDate, StatsDaily> cache = new ConcurrentHashMap<>();

    private LocalDate lastDate;
    private AtomicInteger ticks;

    public StatsManager(Autotip autotip) {
        this.autotip = autotip;
        this.lastDate = LocalDate.now();
        this.ticks = new AtomicInteger(-1);
    }

    public synchronized StatsDaily getToday() {
        return this.getToday(false);
    }

    private synchronized StatsDaily getToday(boolean readOnly) {
        LocalDate now = LocalDate.now();
        if (!lastDate.isEqual(now)) {
            this.save(this.get(lastDate));
            lastDate = now;
        }
        if (!readOnly) {
            // Save after 7 seconds (20 ticks/sec) of no access
            ticks.set(7 * 20);
        }
        return this.get(lastDate);
    }

    /**
     * Get the {@link StatsDaily} for the current date without triggering the auto-save. This method
     * is similar to using {@link #get(LocalDate)} with the {@link LocalDate} being today.
     *
     * @return {@link StatsDaily} of today
     * @see #get(LocalDate)
     */
    public StatsDaily get() {
        return this.getToday(true);
    }

    /**
     * Get the {@link StatsDaily} for the specified date. This method uses a cache to reduce the
     * amount of read/write cycles.
     *
     * @param date The {@link LocalDate} of the StatsDaily you want to get
     * @return {@link StatsDaily} for the specified date
     */
    public StatsDaily get(LocalDate date) {
        if (cache.containsKey(date)) {
            return cache.get(date);
        }
        StatsDaily stats = this.load(new StatsDaily(autotip, date));
        cache.put(date, stats);
        return stats;
    }

    public StatsRange getRange(LocalDate start, LocalDate end) {
        StatsRange range = new StatsRange(autotip, start, end);
        Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .forEach(date -> {
                    range.merge(this.get(date));
                });
        return range;
    }

    public StatsRange getAll() {
        return this.getRange(autotip.getFileUtil().getFirstDate(), LocalDate.now());
    }

    /**
     * Save a {@link StatsDaily} to the current user directory.
     *
     * @param stats The {@link StatsDaily} that you want to save
     */
    public void save(StatsDaily stats) {
        cache.put(stats.getDate(), stats);
        File file = stats.getFile();
        try {
            String json = autotip.getGson().toJson(stats);
            FileUtils.writeStringToFile(file, json, StandardCharsets.UTF_8);
            Autotip.LOGGER.info("Saved " + stats.getFile().getName());
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not write to " + file, e);
        }
    }

    private StatsDaily load(StatsDaily stats) {
        File file = stats.getFile();
        try {
            String json = FileUtils.readFileToString(file);
            return stats.merge(autotip.getGson().fromJson(json, StatsDaily.class));
        } catch (FileNotFoundException e) {
            // Autotip.LOGGER.info(file.getName() + " does not exist, skipping...");
            return stats;
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
            this.save(this.get(lastDate));
            ticks.decrementAndGet();
        }
    }

}
