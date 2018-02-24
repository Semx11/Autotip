package me.semx11.autotip.stats;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.util.NioWrapper;

public class MigrationHelper {

    private static final Pattern STATS_PATTERN = Pattern.compile("\\d{2}-\\d{2}-\\d{4}\\.at");

    public static void loadFiles() {
        Autotip autotip = Autotip.getInstance();
        Path playerPath = NioWrapper.getAutotipPath("stats");
        try {
            if (!NioWrapper.exists(playerPath)) {
                Files.createDirectories(playerPath);
            }
            if (NioWrapper.exists("mods/autotip/options.at")) {
                new MigrationHelper().migrateStats();
            }
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not create directory " + playerPath + "!", e);
            autotip.getConfig().setEnabled(false).save();
        }
    }

    private void migrateStats() {
        Path autotipPath = NioWrapper.getAutotipPath("");
        this.getOldStats(autotipPath).forEach(path -> {
            DailyStatistic stats = DailyStatistic.fromOldFormat(path);
            if (stats != null) {
                stats.save();
            }
        });
    }

    private Set<Path> getOldStats(Path p) {
        try {
            return Files.walk(p, 1)
                    .filter(path -> !path.toFile().isDirectory())
                    .filter(path -> STATS_PATTERN.matcher(path.getFileName().toString()).matches())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not fetch old stats for directory " + p, e);
        }
        return Sets.newHashSet();
    }

    public static void main(String[] args) {
        MigrationHelper helper = new MigrationHelper();
        Path p = NioWrapper.getPath("run/mods/autotip/bf9ee915-aaf5-4c7f-8be0-cc93bba0c187/stats");
        System.out.println(p.toFile().exists());
        helper.getOldStats(p).forEach(path -> {
            DailyStatistic stats = DailyStatistic.fromOldFormat(path);
            if (stats != null) {
                stats.save();
                System.out.println("Saved " + stats.getFileName());
            }
        });
    }

}
