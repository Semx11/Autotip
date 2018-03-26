package me.semx11.autotip.command.impl;

import java.util.Collections;
import java.util.List;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.command.CommandAbstract;
import me.semx11.autotip.chat.MessageUtil;
import net.minecraft.command.ICommandSender;

public class CommandLimbo extends CommandAbstract {

    private boolean executed;

    public CommandLimbo(Autotip autotip) {
        super(autotip);
    }

    public boolean hasExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
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
        MessageUtil messageUtil = autotip.getMessageUtil();

        if (autotip.getSessionManager().isOnHypixel()) {
            this.executed = true;
            messageUtil.sendCommand("/achat \u00a7c");
        } else {
            messageUtil.send("&cYou must be on Hypixel to use this command!");
        }
    }

    @Override
    public List<String> onTabComplete(ICommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
