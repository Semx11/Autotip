package me.semx11.autotip.stats;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.stats.MigrationHelper.LegacyState;
import me.semx11.autotip.util.FileUtil;
import org.apache.commons.io.FileUtils;

public class DailyStatistic {

    private static final Gson GSON = Autotip.getInstance().getGson();
    private static final FileUtil FILE_UTIL = Autotip.getInstance().getFileUtil();

    private static final Pattern TIPS_PATTERN = Pattern
            .compile("^(?<sent>\\d+)(:(?<received>\\d+))?$");
    private static final Pattern GAME_PATTERN = Pattern
            .compile("^(?<game>[\\w\\s]+):(?<sent>\\d+)(:(?<received>\\d+))?$");

    private final LocalDate date;

    private int tipsSent = 0;
    private int tipsReceived = 0;
    private int xpSent = 0;
    private int xpReceived = 0;

    private Map<String, CoinStatistic> gameStatistics = new ConcurrentHashMap<>();

    public DailyStatistic(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
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

    public Map<String, CoinStatistic> getGameStatistics() {
        return gameStatistics;
    }

    public void addCoinsSent(String gameName, int coinsSent) {
        this.addCoins(gameName, coinsSent, 0);
    }

    public void addCoinsReceived(String gameName, int coinsReceived) {
        this.addCoins(gameName, 0, coinsReceived);
    }

    public void addCoins(String gameName, int coinsSent, int coinsReceived) {
        this.addCoins(gameName, new CoinStatistic(coinsSent, coinsReceived));
    }

    private void addCoins(String gameName, CoinStatistic coinStatistic) {
        this.gameStatistics.merge(gameName, coinStatistic, CoinStatistic::merge);
    }

    private File getFile() {
        return FILE_UTIL.getStatsFile(date);
    }

    public DailyStatistic merge(final DailyStatistic that) {
        if (!date.isEqual(that.date)) {
            throw new IllegalArgumentException("Dates do not match!");
        }
        this.tipsSent += that.tipsSent;
        this.tipsReceived += that.tipsReceived;
        this.xpSent += that.xpSent;
        this.xpReceived += that.xpReceived;
        that.gameStatistics.forEach(this::addCoins);
        return this;
    }

    public DailyStatistic save() {
        File file = this.getFile();
        try {
            FileUtils.writeStringToFile(file, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not write to " + file, e);
        }
        return this;
    }

    public DailyStatistic load() {
        File file = this.getFile();
        try {
            String json = FileUtils.readFileToString(file);
            return this.merge(GSON.fromJson(json, DailyStatistic.class));
        } catch (FileNotFoundException e) {
            Autotip.LOGGER.info(file.getName() + " does not exist, creating...");
        } catch (JsonSyntaxException e) {
            Autotip.LOGGER.warn(file.getName() + " has invalid contents, resetting...");
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not read " + file.getName() + "!", e);
        }
        return this.save();
    }

    // TRIGGER WARNING: Legacy code, Hardcoded values, Spaghetti code
    public DailyStatistic migrate(MigrationHelper migrationHelper) {
        // Check if legacy stats file exists
        File file = FILE_UTIL.getLegacyStatsFile(date);
        if (!file.exists()) {
            return this;
        }

        LegacyState state = migrationHelper.getLegacyState(date);

        try {
            // Reads the contents of the file. If the file has less than 2 lines, ignore file.
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() < 2) {
                if (!file.delete()) {
                    Autotip.LOGGER.warn("Could not delete legacy stats file " + file.getName());
                }
                return this;
            }

            // Parses the first line of the file to tips sent and received (e.g. "124:119").
            Matcher tipMatcher = TIPS_PATTERN.matcher(lines.get(0));
            if (tipMatcher.matches()) {
                this.tipsSent = Integer.parseInt(tipMatcher.group("sent"));
                if (tipMatcher.group("received") != null) {
                    this.tipsReceived = Integer.parseInt(tipMatcher.group("received"));
                }
            }

            // This is to fix the wrong tips count in the period between the XP change, and the Autotip fix.
            if (state == LegacyState.BACKTRACK) {
                this.tipsReceived /= 2;
            }

            // Every tip you send is worth 50 XP.
            this.xpSent = tipsSent * 50;
            // This is to account for tips received before the XP change, as they gave you 30 XP, not 60 XP.
            this.xpReceived = (state == LegacyState.BEFORE ? 30 : 60) * tipsReceived;

            // Parses each line with game-data (e.g. "Arcade:2900:2400") to a Map.
            this.gameStatistics = lines.stream()
                    .skip(2)
                    .filter(s -> GAME_PATTERN.matcher(s).matches())
                    .collect(Collectors.toMap(
                            s -> s.split(":")[0],
                            s -> {
                                String[] split = s.split(":");
                                int sent = Integer.parseInt(split[1]);
                                int received = split.length > 2 ? Integer.parseInt(split[2]) : 0;
                                return new CoinStatistic(sent, received);
                            }));

            // Deletes old file to complete migration.
            if (!file.delete()) {
                Autotip.LOGGER.warn("Could not delete legacy stats file " + file.getName());
            }
            return this.save();
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not read file " + file.getName(), e);
            return this.save();
        }
    }

}
