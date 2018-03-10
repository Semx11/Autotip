package me.semx11.autotip.util;

import static me.semx11.autotip.util.NioWrapper.exists;
import static me.semx11.autotip.util.NioWrapper.getAutotipFile;
import static me.semx11.autotip.util.NioWrapper.getAutotipPath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import me.semx11.autotip.misc.TipTracker;
import me.semx11.autotip.misc.Writer;

public class LegacyFileUtil {

    public static void getVars() throws IOException {
        try {
            File statsDir = getAutotipFile("stats");
            if (!statsDir.exists() && !statsDir.mkdirs()) {
                throw new IOException("Could not make required directories");
            }

            boolean executeWriter = false;

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