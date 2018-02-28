package me.semx11.autotip.stats;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.util.Config;
import me.semx11.autotip.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * This class is a collection of all the hacky stuff that has to be accounted for during migration.
 * TRIGGER WARNING: Hardcoded values
 */
public class MigrationHelper {

    private static final DateTimeFormatter OLD_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final LocalDate XP_CHANGE_DATE = LocalDate.of(2016, 11, 29);

    private final FileUtil fileUtil;

    private final Config config;
    private final File legacyConfigFile;
    private final File upgradeDateFile;

    public MigrationHelper(Autotip autotip) {
        this.fileUtil = autotip.getFileUtil();
        this.config = autotip.getConfig();
        this.legacyConfigFile = fileUtil.getFile("options.at");
        this.upgradeDateFile = fileUtil.getFile("upgrade-date.at");
    }

    public boolean hasLegacyFiles() {
        return legacyConfigFile.exists();
    }

    public void migrateLegacyFiles() {
        this.config.migrate();
        this.migrateStats();
    }

    private void migrateStats() {
        try {
            Files.walk(fileUtil.getStatsDir(), 1)
                    .filter(path -> !path.toFile().isDirectory())
                    .map(path -> FilenameUtils.removeExtension(path.getFileName().toString()))
                    .map(filename -> {
                        try {
                            return Optional.of(LocalDate.parse(filename, OLD_FORMAT));
                        } catch (DateTimeParseException e) {
                            return Optional.empty();
                        }
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(date -> new DailyStatistic((LocalDate) date).migrate(this));
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not migrate stats files", e);
        }
    }

    public LegacyState getLegacyState(LocalDate date) {
        if (date.isBefore(XP_CHANGE_DATE)) {
            return LegacyState.BEFORE;
        } else if (date.isBefore(this.getUpgradeDate())) {
            return LegacyState.BACKTRACK;
        } else {
            return LegacyState.AFTER;
        }
    }

    private LocalDate getUpgradeDate() {
        try {
            String date = FileUtils.readFileToString(upgradeDateFile, StandardCharsets.UTF_8);
            return LocalDate.parse(date, OLD_FORMAT);
        } catch (IOException | DateTimeParseException | NullPointerException e) {
            return XP_CHANGE_DATE;
        }
    }

    public enum LegacyState {
        BEFORE, BACKTRACK, AFTER
    }

}
