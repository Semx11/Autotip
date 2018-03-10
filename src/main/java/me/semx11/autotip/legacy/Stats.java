package me.semx11.autotip.legacy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.universal.ChatComponentBuilder;
import me.semx11.autotip.util.ErrorReport;
import me.semx11.autotip.util.MessageUtil;
import me.semx11.autotip.util.NioWrapper;

public class Stats {

    public static void printBetween(String s, String e) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate start = LocalDate.parse(s, formatter);
        LocalDate end = LocalDate.parse(e, formatter);

        List<String> totalDates = new ArrayList<>();
        while (!start.isAfter(end)) {
            totalDates.add(start.format(formatter));
            start = start.plusDays(1);
        }
        printStats(totalDates.toArray(new String[0]));
    }

    public static void printStats(String... days) {
        MessageUtil messageUtil = Autotip.getInstance().getMessageUtil();

        Map<String, Integer> totalStats = new HashMap<>();
        Map<String, Integer> sentStats = new HashMap<>();
        Map<String, Integer> receivedStats = new HashMap<>();

        int[] xp = {0, 0};
        int[] tips = {0, 0};

        for (String date : days) {
            File f = NioWrapper.getAutotipFile("stats/" + date + ".at");
            if (!f.exists()) {
                continue;
            }

            List<Map<String, Integer>> dailyStats = getDailyStats(f);

            dailyStats.get(0).forEach((game, coins) -> {
                if (game.equals("tips")) {
                    xp[0] += 50 * coins;
                    tips[0] += coins;
                } else {
                    totalStats.merge(game, coins, (a, b) -> a + b);
                    sentStats.merge(game, coins, (a, b) -> a + b);
                }
            });
            dailyStats.get(1).forEach((game, coins) -> {
                if (game.equals("tips")) {
                    xp[1] += 60 * coins;
                    tips[1] += coins;
                } else {
                    totalStats.merge(game, coins, (a, b) -> a + b);
                    receivedStats.merge(game, coins, (a, b) -> a + b);
                }
            });
        }

        int karma = 0;
        if (sentStats.containsKey("karma")) {
            karma = sentStats.get("karma");
            sentStats.remove("karma");
        }

        List<String> games = totalStats.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!games.isEmpty()) {
            messageUtil.separator();
            games.forEach(game -> {
                int sentCoins = sentStats.getOrDefault(game, 0);
                int receivedCoins = receivedStats.getOrDefault(game, 0);
                if (sentStats.containsKey(game) || receivedStats.containsKey(game)) {
                    ChatComponentBuilder
                            .of("&a{}: &e{} coins",
                                    game, format(sentCoins + receivedCoins))
                            .setHoverText("&a{}\n"
                                            + "&cBy sending: &e{} coins\n"
                                            + "&9By receiving: &e{} coins",
                                    game, format(sentCoins), format(receivedCoins))
                            .send();
                }
            });
            ChatComponentBuilder
                    .of("&6Tips: {}", format(tips[0] + tips[1]))
                    .setHoverText("&cSent: &6{} tips\n&9Received: &6{} tips",
                            format(tips[0]), format(tips[1]))
                    .send();
            ChatComponentBuilder
                    .of("&9XP: {}", format(xp[0] + xp[1]))
                    .setHoverText("&cBy sending: &9{} XP\n&9By receiving: {} XP",
                            format(xp[0]), format(xp[1]))
                    .send();
            if (karma > 0) {
                ChatComponentBuilder
                        .of("&dKarma: {}", format(karma))
                        .setHoverText("&dI should probably fix this...")
                        .send();
            }

            messageUtil.send("Stats from {}{}",
                    (Object) days[0].replace("-", "/"),
                    days.length > 1 ? " - " + days[days.length - 1].replace("-", "/") : ""
            );
            messageUtil.separator();
        } else {
            messageUtil.send("&cYou have never tipped someone in this period!");
            messageUtil.send("({}{})", (Object) days[0].replace("-", "/"),
                    days.length > 1 ? " - " + days[days.length - 1].replace("-", "/") : "");
        }
    }

    private static List<Map<String, Integer>> getDailyStats(File file) {
        try {
            Map<String, Integer> sentStats = new HashMap<>();
            Map<String, Integer> receivedStats = new HashMap<>();
            try (BufferedReader statsReader = new BufferedReader(new FileReader(file.getPath()))) {
                List<String> lines = statsReader.lines().collect(Collectors.toList());
                if (lines.size() >= 2) {
                    String[] tipStats = lines.get(0).split(":");

                    sentStats.put("tips", Integer.parseInt(tipStats[0]));
                    sentStats.put("karma", Integer.parseInt(lines.get(1)));

                    receivedStats
                            .put("tips", tipStats.length > 1 ? Integer.parseInt(tipStats[1]) : 0);

                    lines.stream().skip(2).forEach(line -> {
                        String stats[] = line.split(":");
                        if (!stats[1].equals("0")) {
                            sentStats.put(stats[0], Integer.parseInt(stats[1]));
                        }
                        if (stats.length > 2 && !stats[2].equals("0")) {
                            receivedStats.put(stats[0], Integer.parseInt(stats[2]));
                        }
                    });
                }
            }
            return Arrays.asList(sentStats, receivedStats);
        } catch (IOException e) {
            ErrorReport.reportException(e);
            return Arrays.asList(Collections.emptyMap(), Collections.emptyMap());
        }
    }

    private static String format(int number) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        return formatter.format(number);
    }

}