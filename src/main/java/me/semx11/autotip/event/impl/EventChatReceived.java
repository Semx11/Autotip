package me.semx11.autotip.event.impl;

import java.util.regex.Pattern;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.chat.Message;
import me.semx11.autotip.chat.MessageMatcher;
import me.semx11.autotip.chat.MessageOption;
import me.semx11.autotip.chat.StatsMessage;
import me.semx11.autotip.chat.StatsMessageMatcher;
import me.semx11.autotip.command.impl.CommandLimbo;
import me.semx11.autotip.config.Config;
import me.semx11.autotip.config.GlobalSettings;
import me.semx11.autotip.event.Event;
import me.semx11.autotip.stats.StatsDaily;
import me.semx11.autotip.universal.UniversalUtil;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventChatReceived implements Event {

    private final Autotip autotip;

    private Pattern xpPattern = Pattern
            .compile("\\+(?<xp>\\d+) experience \\(Gave a player a /tip\\)");
    private Pattern playerPattern = Pattern
            .compile("You tipped (?<player>\\w+) in .*");
    private Pattern coinPattern = Pattern
            .compile("\\+(?<coins>\\d+) coins for you in (?<game>.+) for being generous :\\)");
    private Pattern earnedPattern = Pattern
            .compile("You earned (?<coins>\\d+) coins and (?<xp>\\d+) experience from "
                    + "(?<game>.+) tips in the last minute!");

    // New stuff
    private Pattern tipAllPattern = Pattern
            .compile("You tipped (?<tipsSent>\\d+) players! You got the following rewards:");
    private Pattern newXpPattern = Pattern
            .compile("\\+(?<xpSent>\\d+) Hypixel Experience");
    private Pattern newCoinPattern = Pattern
            .compile("\\+(?<coins>\\d+) (?<game>.+) Coins");

    public EventChatReceived(Autotip autotip) {
        this.autotip = autotip;
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        Config config = autotip.getConfig();

        if (!autotip.getSessionManager().isOnHypixel()) {
            return;
        }

        String msg = UniversalUtil.getUnformattedText(event);

        /*
        +50 experience (Gave a player a /tip)
        You tipped Semx12 in Classic Games
        +15 coins for you in Classic Games for being generous :)
         */

        /*
        You earned 100 coins and 0 experience from VampireZ tips in the last minute!
        You earned 100 coins and 0 experience from Quakecraft tips in the last minute!
        You earned 100 coins and 0 experience from Paintball tips in the last minute!
        You earned 100 coins and 0 experience from Turbo Kart Racers tips in the last minute!
        You earned 100 coins and 0 experience from Arena Brawl tips in the last minute!
        You earned 100 coins and 60 experience from Walls tips in the last minute!
         */

        /*
        You tipped Semx11 in Classic Games
        +50 Hypixel Experience
        +15 VampireZ Coins
        +15 Quakecraft Coins
        +15 Paintball Coins
        +15 Turbo Kart Racers Coins
        +15 Arena Brawl Coins
        +15 Walls Coins
         */

        /*
        You were tipped by 1337 players in the last minute! Rewards:
        +80220 Hypixel Experience
        +2865 Speed UHC Coins
        +2865 UHC Champions Coins
        +2865 Arena Brawl (Classic Games) Coins
        +2865 The Walls (Classic Games) Coins
        +2865 Blitz SG Coins
        +2865 Warlords Coins
        +7258 Arcade Games Coins
         */

        /*
        You tipped 17 players! You got the following rewards:
        +600 Hypixel Experience
        +15 Speed UHC Coins
        +15 UHC Champions Coins
        +15 Arena Brawl Coins
        +15 The Walls Coins
        +15 Blitz SG Coins
        +15 Warlords Coins
        +15 Turbo Kart Racers Coins
        +15 VampireZ Coins
        +15 The TNT Games Coins
        +15 Cops and Crims Coins
        +15 Paintball Warfare Coins
        +38 Arcade Games Coins
        +15 Mega Walls Coins
        +15 SkyClash Coins
        +15 Crazy Walls Coins
        +15 SkyWars Coins
        +15 Quakecraft Coins
         */

        /*
        You tipped 8 players in 12 different games!
        Rewards
        +350 Hypixel Experience
        +15 VampireZ Coins
        +15 Smash Heroes Coins
        +15 The Walls Coins
        +15 Blitz SG Coins
        +15 Warlords Coins
        +15 UHC Champions Coins
        +15 Turbo Kart Racers Coins
        +15 Arena Brawl Coins
        +15 Mega Walls Coins
        +15 Quakecraft Coins
        +15 Speed UHC Coins
        +15 Paintball Warfare Coins
         */

        CommandLimbo limboCommand = autotip.getCommand(CommandLimbo.class);
        if (limboCommand.hasExecuted()) {
            if (msg.startsWith("A kick occurred in your connection")) {
                event.setCanceled(true);
            } else if (msg.startsWith("Illegal characters in chat")) {
                event.setCanceled(true);
                limboCommand.setExecuted(false);
            }
        }

        if (!config.isEnabled()) {
            return;
        }

        GlobalSettings settings = autotip.getGlobalSettings();
        MessageOption option = config.getMessageOption();

        for (Message message : settings.getMessages()) {
            MessageMatcher matcher = message.getMatcherFor(msg);
            if (matcher.matches()) {
                event.setCanceled(message.shouldHide(option));
                return;
            }
        }

        String hover = UniversalUtil.getHoverText(event);

        for (StatsMessage message : settings.getStatsMessages()) {
            StatsMessageMatcher matcher = message.getMatcherFor(msg);
            if (!matcher.matches()) {
                continue;
            }
            StatsDaily stats = this.getStats();
            matcher.applyStats(stats);
            message.applyHoverStats(hover, stats);
            event.setCanceled(message.shouldHide(option));
        }

    }

    private StatsDaily getStats() {
        return this.autotip.getStatsManager().getToday();
    }

}
