package me.semx11.autotip.command;

import java.util.Collections;
import java.util.List;
import me.semx11.autotip.misc.TipTracker;
import me.semx11.autotip.util.MessageUtil;
import me.semx11.autotip.util.TimeUtil;
import net.minecraft.command.ICommandSender;

public class TipHistoryCommand extends AUniversalCommand {

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
                MessageUtil.send("&cInvalid page number.");
            } else {
                MessageUtil.separator();
                MessageUtil.send("&6Tip History &7" + "[Page " + page + " of " + pages + "]&6:");

                TipTracker.tipsSentHistory.entrySet().stream()
                        .skip((page - 1) * 7)
                        .limit(7)
                        .forEach(tip -> MessageUtil.send(tip.getValue() + ": &6" + TimeUtil
                                .formatMillis(System.currentTimeMillis() - tip.getKey()) + "."));

                MessageUtil.separator();
            }
        } else {
            MessageUtil.send("&cYou haven't tipped anyone yet!");
        }
    }

    @Override
    public List<String> onTabComplete(ICommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
