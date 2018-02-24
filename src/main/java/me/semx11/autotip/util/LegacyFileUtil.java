package me.semx11.autotip.util;

import static me.semx11.autotip.util.NioWrapper.exists;
import static me.semx11.autotip.util.NioWrapper.getAutotipFile;
import static me.semx11.autotip.util.NioWrapper.getAutotipPath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import me.semx11.autotip.misc.Stats;
import me.semx11.autotip.misc.TipTracker;
import me.semx11.autotip.misc.Writer;
import org.apache.commons.io.FileUtils;

public class LegacyFileUtil {

    public static void getVars() throws IOException {
        try {
            File statsDir = getAutotipFile("stats");
            if (!statsDir.exists() && !statsDir.mkdirs()) {
                throw new IOException("Could not make required directories");
            }

            Path upgrade = getAutotipPath("upgrade-date.at");
            File file = getAutotipFile("upgrade-date.at");
            if (exists(upgrade)) {
                String date = FileUtils.readFileToString(upgrade.toFile());
                LocalDate parsed;
                try {
                    parsed = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                } catch (DateTimeParseException e) {
                    ErrorReport.reportException(e);
                    parsed = LocalDate.now();
                }
                Stats.setUpgradeDate(parsed);
            } else {
                LocalDate date = LocalDate.now().plusDays(1);
                String dateString = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(date);
                FileUtils.writeStringToFile(upgrade.toFile(), dateString);
                Stats.setUpgradeDate(date);
            }

            boolean executeWriter = false;

            Path options = getAutotipPath("options.at");
            if (exists(options)) {
                List<String> lines = Files.lines(options).collect(Collectors.toList());
                if (lines.size() >= 4) {
                    //Autotip.toggle = Boolean.parseBoolean(lines.get(0));
                    String chatSetting = lines.get(1);
                    switch (chatSetting) {
                        case "true":
                        case "false":
                            /*Autotip.messageOption = Boolean.parseBoolean(chatSetting)
                                    ? MessageOption.SHOWN
                                    : MessageOption.COMPACT;*/
                            break;
                        case "SHOWN":
                        case "COMPACT":
                        case "HIDDEN":
                            //Autotip.messageOption = MessageOption.valueOf(chatSetting);
                            break;
                        default:
                            //Autotip.messageOption = MessageOption.SHOWN;
                    }
                    try {
                        //Autotip.totalTipsSent = Integer.parseInt(lines.get(3));
                    } catch (NumberFormatException e) {
                        //Autotip.totalTipsSent = 0;
                        executeWriter = true;
                    }
                } else {
                    executeWriter = true;
                }
            } else {
                executeWriter = true;
            }

            Path today = getAutotipPath("stats/" + getDate() + ".at");
            if (exists(today)) {
                List<String> lines = Files.lines(today).collect(Collectors.toList());
                if (lines.size() >= 2) {
                    String[] tipStats = lines.get(0).split(":");
                    TipTracker.tipsSent = Integer.parseInt(tipStats[0]);
                    TipTracker.tipsReceived =
                            tipStats.length > 1 ? Integer.parseInt(tipStats[1]) : 0;
                    TipTracker.karmaCount = Integer.parseInt(lines.get(1));
                    lines.stream().skip(2).forEach(line -> {
                        String[] stats = line.split(":");
                        TipTracker.tipsSentEarnings.put(stats[0], Integer.parseInt(stats[1]));
                        if (stats.length > 2) {
                            TipTracker.tipsReceivedEarnings
                                    .put(stats[0], Integer.parseInt(stats[2]));
                        }
                    });
                }
            } else {
                executeWriter = true;
            }

            if (executeWriter) {
                Writer.execute();
            }
        } catch (IOException | IllegalArgumentException e) {
            ErrorReport.reportException(e);
        }
    }

    public static String getDate() {
        return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    }

}