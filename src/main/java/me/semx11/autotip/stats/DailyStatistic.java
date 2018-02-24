package me.semx11.autotip.stats;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.util.NioWrapper;
import org.apache.commons.io.FileUtils;

public class DailyStatistic {

    private static final Gson GSON = Autotip.getInstance().getGson();

    private static Pattern datePattern = Pattern
            .compile("(?<d>\\d{2})-(?<m>\\d{2})-(?<y>\\d{4})\\.at");
    private static Pattern tipsPattern = Pattern.compile("^(?<sent>\\d+)(:(?<received>\\d+))?$");
    private static Pattern gamePattern = Pattern
            .compile("^(?<game>[a-zA-Z\\s]+):(?<sent>\\d+)(:(?<received>\\d+))?$");

    private LocalDate date;
    private int tipsSent;
    private int tipsReceived;
    private int karmaEarned;

    private Map<String, CoinStatistic> gameStatistics;

    public DailyStatistic(LocalDate date, int tipsSent, int tipsReceived, int karmaEarned) {
        this(date, tipsSent, tipsReceived, karmaEarned, Collections.emptyMap());
    }

    public DailyStatistic(LocalDate date, int tipsSent, int tipsReceived, int karmaEarned,
            Map<String, CoinStatistic> gameStatistics) {
        this.date = date;
        this.tipsSent = tipsSent;
        this.tipsReceived = tipsReceived;
        this.karmaEarned = karmaEarned;
        this.gameStatistics = gameStatistics;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getFileName() {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".at";
    }

    public int getTipsSent() {
        return tipsSent;
    }

    public void addTipSent() {
        tipsSent++;
    }

    public int getTipsReceived() {
        return tipsReceived;
    }

    public void addTipsReceived(int tips) {
        tipsReceived += tips;
    }

    public int getKarmaEarned() {
        return karmaEarned;
    }

    public void addCoinsSent(String gameName, int coinsSent) {
        addCoins(gameName, coinsSent, 0);
    }

    public void addCoinsReceived(String gameName, int coinsReceived) {
        addCoins(gameName, 0, coinsReceived);
    }

    public void addCoins(String gameName, int coinsSent, int coinsReceived) {
        addCoins(gameName, new CoinStatistic(coinsSent, coinsReceived));
    }

    private void addCoins(String gameName, CoinStatistic coinStatistic) {
        gameStatistics.merge(gameName, coinStatistic, CoinStatistic::merge);
    }

    public Map<String, CoinStatistic> getGameStatistics() {
        return gameStatistics;
    }

    public DailyStatistic merge(final DailyStatistic that) {
        if (!date.isEqual(that.getDate())) {
            return this;
        }
        this.tipsSent += that.tipsSent;
        this.tipsReceived += that.tipsReceived;
        this.karmaEarned += that.karmaEarned;
        that.gameStatistics.forEach(this::addCoins);
        return this;
    }

    public DailyStatistic save() {
        File statsFile = NioWrapper.getAutotipFile("stats/" + this.getFileName());
        try {
            FileUtils.writeStringToFile(statsFile, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not write to " + statsFile, e);
        }
        return this;
    }

    public static DailyStatistic get(LocalDate date) {
        NioWrapper.exists(Autotip.getInstance().getUserDirString() + "stats/" + date.toString());
        return null;
    }

    public static DailyStatistic fromOldFormat(Path path) {

        // Parses the filename (e.g. 31-12-2015) to a LocalDate. Returns null if parsing failed.
        Matcher dateMatcher = datePattern.matcher(path.getFileName().toString());
        if (!dateMatcher.matches()) {
            return null;
        }
        int day = Integer.parseInt(dateMatcher.group("d"));
        int month = Integer.parseInt(dateMatcher.group("m"));
        int year = Integer.parseInt(dateMatcher.group("y"));

        // Initial variables.
        int tipsSent = 0;
        int tipsReceived = 0;
        int karmaEarned = 0;

        try {
            // Reads the contents of the file. If the file has less than 2 lines, it return null.
            List<String> lines = Files.readAllLines(path);
            if (lines.size() < 2) {
                return null;
            }

            // Parses the first line of the file to tips sent and received (e.g. "124:119").
            Matcher tipMatcher = tipsPattern.matcher(lines.get(0));
            if (tipMatcher.matches()) {
                tipsSent = Integer.parseInt(tipMatcher.group("sent"));
                if (tipMatcher.group("received") != null) {
                    tipsReceived = Integer.parseInt(tipMatcher.group("received"));
                }
            }

            // Parses karma.
            try {
                karmaEarned = Integer.parseInt(lines.get(1));
            } catch (NumberFormatException ignored) {
            }

            // If the file has less than 3 lines (so no game-data), it will skip that.
            if (lines.size() < 3) {
                return new DailyStatistic(LocalDate.of(year, month, day), tipsSent, tipsReceived,
                        karmaEarned);
            }

            // Parses each line with game-data (e.g. "Arcade:2900:2400") to a Map with the gameName and a CoinStatistic.
            Map<String, CoinStatistic> gameStatistics = lines.stream().skip(2).filter(s -> {
                Matcher gameMatcher = gamePattern.matcher(s);
                return gameMatcher.matches();
            }).collect(Collectors.toMap(
                    s -> s.split(":")[0],
                    s -> {
                        Matcher gameMatcher = gamePattern.matcher(s);
                        gameMatcher.matches();
                        int sent = 0;
                        int received = 0;
                        if (gameMatcher.group("received") != null) {
                            received = Integer.parseInt(gameMatcher.group("received"));
                        }
                        sent = Integer.parseInt(gameMatcher.group("sent"));
                        return new CoinStatistic(sent, received);
                    }
            ));
            return new DailyStatistic(LocalDate.of(year, month, day), tipsSent, tipsReceived,
                    karmaEarned, gameStatistics);
        } catch (IOException e) {
            // IOException is thrown if there was an error reading the file.
            Autotip.LOGGER.error("Could not read file " + path, e);
            return null;
        } catch (DateTimeException e) {
            // DateTimeException is thrown if the filename contained an illegal date (e.g. "40-20-2015").
            Autotip.LOGGER.error("Illegal date format (" + path + ")", e);
            return null;
        }
    }

}
