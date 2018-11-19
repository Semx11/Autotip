package me.semx11.autotip.event.impl;

import me.semx11.autotip.Autotip;
import me.semx11.autotip.message.Message;
import me.semx11.autotip.message.MessageMatcher;
import me.semx11.autotip.chat.MessageOption;
import me.semx11.autotip.message.StatsMessage;
import me.semx11.autotip.message.StatsMessageMatcher;
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
