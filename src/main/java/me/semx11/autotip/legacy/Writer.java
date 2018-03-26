package me.semx11.autotip.legacy;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.util.ErrorReport;

public class Writer implements Runnable {

    private static String lastDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

    public static void execute() {
        Autotip.getInstance().getTaskManager().getExecutor().execute(new Writer());
    }

    @Override
    public void run() {
        Autotip autotip = Autotip.getInstance();
        try {
            if (!lastDate.equals(LegacyFileUtil.getDate())) {
                TipTracker.tipsSent = 0;
                TipTracker.tipsReceived = 0;
                TipTracker.tipsSentEarnings.clear();
                TipTracker.tipsReceivedEarnings.clear();
            }

            /*
            FileWriter dailyStats = new FileWriter(NioWrapper
                    .separator(autotip.getUserDirString() + "stats/" + LegacyFileUtil.getDate()
                            + ".at"));

            write(dailyStats, TipTracker.tipsSent + ":" + TipTracker.tipsReceived);
            write(dailyStats, "0");

            // Prevent ConcurrentModificationException until new format.
            Set<String> sent = new HashSet<>(TipTracker.tipsSentEarnings.keySet());
            Set<String> received = new HashSet<>(TipTracker.tipsReceivedEarnings.keySet());

            List<String> games = Stream.concat(sent.stream(), received.stream())
                    .distinct()
                    .collect(Collectors.toList());

            games.forEach(game -> {
                int sentCoins = TipTracker.tipsSentEarnings.getOrDefault(game, 0);
                int receivedCoins = TipTracker.tipsReceivedEarnings.getOrDefault(game, 0);
                write(dailyStats, game + ":" + sentCoins + ":" + receivedCoins);
            });
            dailyStats.close();*/

            lastDate = LegacyFileUtil.getDate();

        } catch (/*IO*/Exception e) {
            ErrorReport.reportException(e);
        }
    }

    private void write(FileWriter writer, String text) {
        try {
            writer.write(text + System.lineSeparator());
        } catch (IOException e) {
            ErrorReport.reportException(e);
        }
    }

}