package me.semx11.autotip.command;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.event.EventClientConnection;
import me.semx11.autotip.event.EventClientTick;
import me.semx11.autotip.misc.Stats;
import me.semx11.autotip.misc.TipTracker;
import me.semx11.autotip.util.ChatColor;
import me.semx11.autotip.util.FileUtil;
import me.semx11.autotip.util.MessageUtil;
import me.semx11.autotip.util.MinecraftVersion;
import me.semx11.autotip.util.Versions;
import net.minecraft.command.ICommandSender;
import org.apache.commons.lang3.StringUtils;

public class AutotipCommand extends AUniversalCommand {

    @Override
    public String getCommandName() {
        return "autotip";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/autotip <stats, info, messages, toggle, time>";
    }

    @Override
    public List<String> getCommandAliases() {
        if (!Autotip.MC_VERSION.equals(MinecraftVersion.V1_8)) {
            return Collections.singletonList("at");
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void onCommand(ICommandSender sender, String[] args) {

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "m":
                case "messages":
                    Autotip.messageOption = Autotip.messageOption.next();
                    MessageUtil.send("Tip Messages: " + Autotip.messageOption);
                    break;
                case "?":
                case "info":
                    MessageUtil.separator();
                    MessageUtil
                            .send("&6&lAutotip v" + Autotip.VERSION + " &7by &b[MVP&4+&b] Semx11");
                    MessageUtil.send("Brought to you by the &6Autotip Team &7(hover)",
                            null,
                            "&b[MVP&4+&b] Semx11 &r&7- Lead Mod developer, API, PR\n"
                                    + "&b[MVP&3+&b] 2Pi &r&7- Lead API developer, Founder, Creator, PR\n"
                                    + "&6[YT] Sk1er &r&7- Host, not the creator, PR");
                    MessageUtil.send("Autotipper: " + (Autotip.toggle ? "&aEn" : "&cDis") +
                            "abled");
                    MessageUtil.send("Tip Messages: " + Autotip.messageOption);
                    MessageUtil.send("Tips sent today: " + ChatColor.GOLD + TipTracker.tipsSent);
                    MessageUtil.send("Tips received today: " + ChatColor.GOLD
                            + TipTracker.tipsReceived);
                    MessageUtil
                            .send("Lifetime tips sent: " + ChatColor.GOLD + Autotip.totalTipsSent);
                    MessageUtil.send(ChatColor.GOLD
                            + "Type /autotip stats to see what has been earned.");
                    MessageUtil.separator();
                    break;
                case "s":
                case "stats":
                    LocalDate now = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                    if (args.length == 2) {
                        switch (args[1].toLowerCase()) {
                            case "day":
                            case "daily":
                            case "today":
                                Stats.printStats(FileUtil.getDate());
                                break;
                            case "yesterday":
                                Stats.printStats(now.minusDays(1).format(formatter));
                                break;
                            case "week":
                            case "weekly":
                                Stats.printBetween(now.with(DayOfWeek.MONDAY).format(formatter),
                                        now.with(DayOfWeek.SUNDAY).format(formatter));
                                break;
                            case "month":
                            case "monthly":
                                Stats.printBetween(now.withDayOfMonth(1).format(formatter),
                                        now.withDayOfMonth(now.lengthOfMonth()).format(formatter));
                                break;
                            case "year":
                            case "yearly":
                                Stats.printBetween("01-01-" + Year.now().getValue(),
                                        "31-12-" + Year.now().getValue());
                                break;
                            case "all":
                            case "total":
                            case "life":
                            case "lifetime":
                                Stats.printBetween("25-06-2016", FileUtil.getDate());
                                break;
                            default:
                                MessageUtil.send(ChatColor.RED
                                        + "Usage: /autotip stats <day, week, month, year, lifetime>");
                                break;
                        }
                    } else {
                        Stats.printStats(FileUtil.getDate());
                    }
                    break;
                case "t":
                case "toggle":
                    Autotip.toggle = !Autotip.toggle;
                    MessageUtil.send(
                            "Autotipper: " + (Autotip.toggle ? ChatColor.GREEN + "En"
                                    : ChatColor.RED + "Dis") + "abled");
                    break;
                case "wave":
                case "time":
                    if (Autotip.toggle) {
                        if (Autotip.onHypixel) {
                            MessageUtil.separator();
                            MessageUtil.send("Last wave: " +
                                    ChatColor.GOLD + LocalTime.MIN
                                    .plusSeconds(EventClientTick.waveCounter)
                                    .toString());
                            MessageUtil.send("Next wave: " +
                                    ChatColor.GOLD + LocalTime.MIN.plusSeconds(
                                    EventClientTick.waveLength - EventClientTick.waveCounter)
                                    .toString());
                            MessageUtil.separator();
                        } else {
                            MessageUtil
                                    .send("Autotip is disabled as you are not playing on Hypixel.");
                        }
                    } else {
                        MessageUtil.send("Autotip is disabled. Use " + ChatColor.GOLD
                                + "/autotip toggle"
                                + ChatColor.GRAY + " to enable it.");
                    }
                    break;
                case "whatsnew":
                    MessageUtil.separator();
                    MessageUtil.send(ChatColor.GOLD + "What's new in Autotip v" + Autotip.VERSION
                            + ":");
                    Versions.getInstance().getInfoByVersion(Autotip.VERSION).getChangelog().forEach(
                            s -> MessageUtil
                                    .send(ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + s));
                    MessageUtil.separator();
                    break;
                case "info+":
                    MessageUtil.separator();
                    MessageUtil.send("Last IP joined: " + EventClientConnection.lastIp);
                    MessageUtil.send("Detected MC version: " + Autotip.MC_VERSION);
                    MessageUtil
                            .send("Current tipqueue: " + StringUtils
                                    .join(EventClientTick.TIP_QUEUE.iterator(), ", "));
                    MessageUtil.separator();
                    break;
                default:
                    MessageUtil.send(ChatColor.RED + "Usage: " + getCommandUsage(sender));
                    break;
            }
        } else {
            MessageUtil.send(ChatColor.RED + "Usage: " + getCommandUsage(sender));
        }
    }

    @Override
    public List<String> onTabComplete(ICommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, "stats", "info", "messages", "toggle",
                        "time");
            case 2:
                if (args[0].equalsIgnoreCase("stats") || args[0].equalsIgnoreCase("s")) {
                    return getListOfStringsMatchingLastWord(args, "day", "yesterday", "week",
                            "month", "year",
                            "lifetime");
                }
        }
        return Collections.emptyList();
    }
}
