package me.semx11.autotip;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import me.semx11.autotip.api.SessionKey;
import me.semx11.autotip.command.AUniversalCommand;
import me.semx11.autotip.command.AutotipCommand;
import me.semx11.autotip.command.LimboCommand;
import me.semx11.autotip.command.TipHistoryCommand;
import me.semx11.autotip.event.EventChatReceived;
import me.semx11.autotip.event.EventClientConnection;
import me.semx11.autotip.event.EventClientTick;
import me.semx11.autotip.util.FileUtil;
import me.semx11.autotip.util.Hosts;
import me.semx11.autotip.util.MessageOption;
import me.semx11.autotip.util.MinecraftVersion;
import me.semx11.autotip.util.NioWrapper;
import me.semx11.autotip.util.UniversalUtil;
import me.semx11.autotip.util.Version;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Autotip.MODID, version = Autotip.VERSION_STRING, clientSideOnly = true, acceptedMinecraftVersions = "[1.8, 1.12.1]")
public class Autotip {

    // TODO: Remove upon release
    public static final boolean BETA = true;

    public static final String MODID = "autotip";
    public static final String VERSION_STRING = "2.1.0.2";
    public static final Version VERSION = new Version(VERSION_STRING);

    public static final Minecraft MC = Minecraft.getMinecraft();
    public static final MinecraftVersion MC_VERSION = UniversalUtil.getMinecraftVersion();

    public static final Logger LOGGER = LogManager.getLogger("Autotip");
    public static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(3);
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("AutotipThread").build());

    public static final String USER_DIR = NioWrapper
            .separator("mods/autotip/" + MC.getSession().getProfile().getId() + "/");

    public static MessageOption messageOption = MessageOption.SHOWN;
    public static boolean onHypixel = false;
    public static boolean toggle = true;

    public static int totalTipsSent;

    private static SessionKey sessionKey;

    public static boolean hasSessionKey() {
        return sessionKey != null;
    }

    public static SessionKey getSessionKey() {
        return sessionKey;
    }

    public static void setSessionKey(SessionKey sessionKey) {
        Autotip.sessionKey = sessionKey;
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        try {
            this.registerEvents(
                    new EventClientTick(),
                    new EventClientConnection(),
                    new EventChatReceived()
            );
            this.registerCommands(
                    new AutotipCommand(),
                    new TipHistoryCommand(),
                    new LimboCommand()
            );
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