package me.semx11.autotip.command;

import java.util.Collections;
import java.util.List;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.util.MessageUtil;
import net.minecraft.command.ICommandSender;

public class LimboCommand extends AUniversalCommand {

    private static boolean executed;

    public static boolean hasExecuted() {
        return executed;
    }

    public static void setExecuted(boolean executed) {
        LimboCommand.executed = executed;
    }

    @Override
    public String getCommandName() {
        return "limbo";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/limbo";
    }

    @Override
    public void onCommand(ICommandSender sender, String[] args) {
        if (Autotip.SESSION_MANAGER.isOnHypixel()) {
            executed = true;
            MessageUtil.sendCommand("/achat \u00a7c");
        } else {
            MessageUtil.send("&cYou must be on Hypixel to use this command!");
        }
    }

    @Override
    public List<String> onTabComplete(ICommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
