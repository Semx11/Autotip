package me.semx11.autotip.command.impl;

import java.util.Collections;
import java.util.List;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.command.CommandAbstract;
import me.semx11.autotip.misc.TipTracker;
import me.semx11.autotip.util.MessageUtil;
import me.semx11.autotip.util.TimeUtil;
import net.minecraft.command.ICommandSender;

public class CommandTipHistory extends CommandAbstract {

    public CommandTipHistory(Autotip autotip) {
        super(autotip);
    }

    @Override
    public String getCommandName() {
        return "tiphistory";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/tiphistory [page]";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("lasttip");
    }

    @Override
    public void onCommand(ICommandSender sender, String[] args) {
        MessageUtil messageUtil = autotip.getMessageUtil();

        if (TipTracker.tipsSentHistory.size() > 0) {
            int page = 1;
            int pages = (int) Math.ceil((double) TipTracker.tipsSentHistory.size() / 7.0);

            if (args.length > 0) {
                try {
                    page = Integer.parseInt(args[0]);
                } catch (NumberFormatException ignored) {
                    page = -1;
                }
            }

            if (page < 1 || page > pages) {
                messageUtil.send("&cInvalid page number.");
            } else {
                messageUtil.separator();
                messageUtil.send("&6Tip History &7" + "[Page " + page + " of " + pages + "]&6:");

                TipTracker.tipsSentHistory.entrySet().stream()
                        .skip((page - 1) * 7)
                        .limit(7)
                        .forEach(tip -> messageUtil.send(tip.getValue() + ": &6" + TimeUtil
                                .formatMillis(System.currentTimeMillis() - tip.getKey()) + "."));

                messageUtil.separator();
            }
        } else {
            messageUtil.send("&cYou haven't tipped anyone yet!");
        }
    }

    @Override
    public List<String> onTabComplete(ICommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
