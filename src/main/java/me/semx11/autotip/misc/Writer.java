package me.semx11.autotip.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.core.TaskManager;
import me.semx11.autotip.util.ErrorReport;
import me.semx11.autotip.util.FileUtil;

public class Writer implements Runnable {

    private static String lastDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    private static String ls = System.lineSeparator();

    public static void execute() {
        TaskManager.EXECUTOR.execute(new Writer());
    }

    @Override
    public void run() {
        try {
            try (FileWriter writeOptions = new FileWriter(Autotip.USER_DIR + "options.at")) {
                write(writeOptions, Autotip.toggle + ls);
                write(writeOptions, Autotip.messageOption.name() + ls);
                write(writeOptions, "true" + ls);
                write(writeOptions, Autotip.totalTipsSent + ls);
            }

            if (!lastDate.equals(FileUtil.getDate())) {
                TipTracker.tipsSent = 0;
                TipTracker.tipsReceived = 0;
                TipTracker.tipsSentEarnings.clear();
                TipTracker.tipsReceivedEarnings.clear();
            }

            FileWriter dailyStats = new FileWriter(
                    Autotip.USER_DIR + "stats" + File.separator + FileUtil.getDate() + ".at");

            write(dailyStats, TipTracker.tipsSent + ":" + TipTracker.tipsReceived + ls);
            write(dailyStats, "0" + ls);

            // Prevent ConcurrentModificationException until new format.
            Set<String> sent = new HashSet<>(TipTracker.tipsSentEarnings.keySet());
            Set<String> received = new HashSet<>(TipTracker.tipsReceivedEarnings.keySet());

            List<String> games = Stream.concat(sent.stream(), received.stream())
                    .distinct()
                    .collect(Collectors.toList());

            games.forEach(game -> {
                int sentCoins = TipTracker.tipsSentEarnings.getOrDefault(game, 0);
                int receivedCoins = TipTracker.tipsReceivedEarnings.getOrDefault(game, 0);
                write(dailyStats, game + ":" + sentCoins + ":" + receivedCoins + ls);
            });
            dailyStats.close();

            lastDate = FileUtil.getDate();

        } catch (IOException e) {
            ErrorReport.reportException(e);
        }
    }

    private void write(FileWriter writer, String text) {
        try {
            writer.write(text);
        } catch (IOException e) {
            ErrorReport.reportException(e);
        }
    }

}