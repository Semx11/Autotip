package me.semx11.autotip.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

            List<String> games = Stream.concat(
                    TipTracker.tipsSentEarnings.keySet().stream(),
                    TipTracker.tipsReceivedEarnings.keySet().stream()
            ).distinct().collect(Collectors.toList());

            games.forEach(game -> {
                int sent = TipTracker.tipsSentEarnings.getOrDefault(game, 0);
                int received = TipTracker.tipsReceivedEarnings.getOrDefault(game, 0);
                write(dailyStats, game + ":" + sent + ":" + received + ls);
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