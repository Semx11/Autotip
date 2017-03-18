package me.semx11.autotip.command;

import me.semx11.autotip.Autotip;
import me.semx11.autotip.util.ChatColor;
import me.semx11.autotip.util.ClientMessage;
import net.minecraft.command.ICommandSender;

import java.util.Collections;
import java.util.List;

public class LimboCommand extends AUniversalCommand {

    public static boolean executed;

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
        return "/lasttip";
    }

    @Override
    public void onCommand(ICommandSender sender, String[] args) {
        if (Autotip.onHypixel) {
            executed = true;
            Autotip.mc.thePlayer.sendChatMessage(ChatColor.RED.toString());
        } else ClientMessage.send(ChatColor.RED + "You must be on Hypixel to use this command!");
    }

    @Override
    public List<String> onTabComplete(ICommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
