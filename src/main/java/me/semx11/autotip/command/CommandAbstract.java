package me.semx11.autotip.command;

import java.util.List;
import javax.annotation.Nullable;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.core.TaskManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public abstract class CommandAbstract extends CommandBase {

    private static final Autotip AUTOTIP = Autotip.getInstance();

    // Minecraft 1.8 | 1.8.8 | 1.8.9
    // func_71515_b
    public void processCommand(ICommandSender sender, String[] args) {
        AUTOTIP.getTaskManager().getExecutor().execute(() -> onCommand(AUTOTIP, sender, args));
    }

    // Minecraft 1.9 | 1.9.4 | 1.10 | 1.10.2 | 1.11 | 1.11.2 | 1.12 | 1.12.1 | 1.12.2
    // func_184881_a
    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException {
        AUTOTIP.getTaskManager().getExecutor().execute(() -> onCommand(AUTOTIP, sender, args));
    }

    // Minecraft 1.8 | 1.8.8 | 1.8.9
    // func_180525_a
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args,
            BlockPos pos) {
        return onTabComplete(sender, args);
    }

    // Minecraft 1.9 | 1.9.4 | 1.10 | 1.10.2 | 1.11 | 1.11.2 | 1.12 | 1.12.1 | 1.12.2
    // func_184883_a
    public List<String> func_184883_a(MinecraftServer server, ICommandSender sender, String[] args,
            @Nullable BlockPos pos) {
        return onTabComplete(sender, args);
    }

    public abstract void onCommand(Autotip autotip, ICommandSender sender, String[] args);

    public abstract List<String> onTabComplete(ICommandSender sender, String[] args);

}