package me.semx11.autotip.event;

import static me.semx11.autotip.util.MessageOption.COMPACT;
import static me.semx11.autotip.util.MessageOption.HIDDEN;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.command.LimboCommand;
import me.semx11.autotip.misc.TipTracker;
import me.semx11.autotip.misc.Writer;
import me.semx11.autotip.util.ChatColor;
import me.semx11.autotip.util.ClientMessage;
import me.semx11.autotip.util.MessageOption;
import me.semx11.autotip.util.UniversalUtil;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventChatReceived {

    private Pattern xpPattern = Pattern.compile("\\+50 experience \\(Gave a player a /tip\\)");
    private Pattern playerPattern = Pattern.compile("You tipped (?<player>\\w+) in .*");
    private Pattern coinPattern = Pattern.compile(
            "\\+(?<coins>\\d+) coins for you in (?<game>.+) for being generous :\\)");
    private Pattern earnedPattern = Pattern.compile(
            "You earned (?<coins>\\d+) coins and (?<xp>\\d+) experience from (?<game>.+) tips in the last minute!");

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {

        if (!Autotip.onHypixel) {
            return;
        }

        String msg = UniversalUtil.getUnformattedText(event);
        MessageOption mOption = Autotip.messageOption;

        if (Autotip.toggle) {
            if (msg.equals("Slow down! You can only use /tip every few seconds.")
                    || msg.equals("Still processing your most recent request!")
                    || msg.equals("You are not allowed to use commands as a spectator!")
                    || msg.equals("You cannot tip yourself!")
                    || msg.startsWith("You can only use the /tip command")
                    || msg.startsWith("You can't tip the same person")
                    || msg.startsWith("You've already tipped someone in the past hour in ")
                    || msg.startsWith("You've already tipped that person")) {
                event.setCanceled(true);
            }

            if (xpPattern.matcher(msg).matches()) {
                event.setCanceled(mOption.equals(COMPACT) || mOption.equals(HIDDEN));
                return;
            }

            Matcher playerMatcher = playerPattern.matcher(msg);
            if (playerMatcher.matches()) {
                TipTracker.addTip(playerMatcher.group("player"));
                event.setCanceled(mOption.equals(HIDDEN));
                return;
            }

            Matcher coinMatcher = coinPattern.matcher(msg);
            if (coinMatcher.matches()) {
                int coins = Integer.parseInt(coinMatcher.group("coins"));
                String game = coinMatcher.group("game");

                TipTracker.tipsSentEarnings.merge(game, coins, (a, b) -> a + b);
                event.setCanceled(mOption.equals(COMPACT) || mOption.equals(HIDDEN));

                return;
            }

            Matcher earnedMatcher = earnedPattern.matcher(msg);
            if (earnedMatcher.matches()) {
                int coins = Integer.parseInt(earnedMatcher.group("coins"));
                int xp = Integer.parseInt(earnedMatcher.group("xp"));
                String game = earnedMatcher.group("game");

                TipTracker.tipsReceivedEarnings.merge(game, coins, (a, b) -> a + b);
                TipTracker.tipsReceived += xp / 60;
                Writer.execute();

                if (mOption.equals(COMPACT)) {
                    ClientMessage.sendRaw(
                            String.format("%sEarned %s%d coins%s and %s%d experience%s in %s.",
                                    ChatColor.GREEN, ChatColor.YELLOW, coins,
                                    ChatColor.GREEN, ChatColor.BLUE, xp,
                                    ChatColor.GREEN, game
                            ));
                }
                event.setCanceled(mOption.equals(COMPACT) || mOption.equals(HIDDEN));
                Autotip.LOGGER.info("Earned {} coins and {} experience in {}.", coins, xp, game);
                return;
            }
        }

        if (LimboCommand.executed) {
            if (msg.startsWith("A kick occurred in your connection")
                    || msg.startsWith("Illegal characters in chat")) {
                event.setCanceled(true);
                LimboCommand.executed = false;
            }
        }

    }
}
