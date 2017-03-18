package me.semx11.autotip;

import me.semx11.autotip.command.AUniversalCommand;
import me.semx11.autotip.command.AutotipCommand;
import me.semx11.autotip.command.LimboCommand;
import me.semx11.autotip.command.TipHistoryCommand;
import me.semx11.autotip.event.ChatListener;
import me.semx11.autotip.event.HypixelListener;
import me.semx11.autotip.event.Tipper;
import me.semx11.autotip.misc.AutotipThreadFactory;
import me.semx11.autotip.util.FileUtil;
import me.semx11.autotip.util.Hosts;
import me.semx11.autotip.util.MessageOption;
import me.semx11.autotip.util.MinecraftVersion;
import me.semx11.autotip.util.UniversalUtil;
import me.semx11.autotip.util.Version;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mod(modid = Autotip.MODID, version = Autotip.VERSION_STRING, clientSideOnly = true, acceptedMinecraftVersions = "[1.8, 1.11.2]")
public class Autotip {

    public static final String MODID = "autotip";
    public static final String VERSION_STRING = "2.0.3";
    public static final Version VERSION = new Version(VERSION_STRING);
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new AutotipThreadFactory());
    public static String USER_DIR = "";

    public static MinecraftVersion MC_VERSION;
    public static Minecraft mc = Minecraft.getMinecraft();

    public static MessageOption messageOption = MessageOption.SHOWN;
    public static String playerUUID = "";
    public static boolean onHypixel = false;
    public static boolean toggle = true;

    public static int totalTipsSent;
    public static List<String> alreadyTipped = new ArrayList<>();

    @EventHandler
    public void init(FMLInitializationEvent event) {
        try {
            MC_VERSION = MinecraftVersion.fromString(UniversalUtil.getMinecraftVersion());
            playerUUID = Minecraft.getMinecraft().getSession().getProfile().getId().toString();
            USER_DIR = "mods" + File.separator + "autotip" + File.separator + playerUUID + File.separator;

            registerEvents(this, new Tipper(), new HypixelListener(), new ChatListener());
            registerCommands(new AutotipCommand(), new TipHistoryCommand(), new LimboCommand());

            FileUtil.getVars();
            Hosts.updateHosts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerEvents(Object... events) {
        Arrays.asList(events).forEach((event) -> {
            MinecraftForge.EVENT_BUS.register(event);
            FMLCommonHandler.instance().bus().register(event);
        });
    }

    private void registerCommands(AUniversalCommand... commands) {
        Arrays.asList(commands).forEach(ClientCommandHandler.instance::registerCommand);
    }

}