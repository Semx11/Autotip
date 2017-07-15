package me.semx11.autotip.util;

import static me.semx11.autotip.util.ReflectionUtil.getClazz;
import static me.semx11.autotip.util.ReflectionUtil.getConstructor;
import static me.semx11.autotip.util.ReflectionUtil.getEnum;
import static me.semx11.autotip.util.ReflectionUtil.getField;
import static me.semx11.autotip.util.ReflectionUtil.getMethod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketAddress;
import me.semx11.autotip.Autotip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class UniversalUtil {

    public static String getMinecraftVersion() {
        try {
            Field f = getField(ForgeVersion.class, "mcVersion");
            if (f != null) {
                return (String) f.get(null);
            } else {
                return "1.8";
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return "1.8";
        }
    }

    public static SocketAddress getRemoteAddress(ClientConnectedToServerEvent event) {
        SocketAddress address = null;
        try {
            Object networkManager = null;
            switch (Autotip.MC_VERSION) {
                case V1_8:
                case V1_8_8:
                case V1_8_9:
                    networkManager = getField(ClientConnectedToServerEvent.class,
                            "manager").get(event);
                    break;
                case V1_9:
                case V1_9_4:
                case V1_10:
                case V1_10_2:
                case V1_11:
                case V1_11_2:
                case V1_12:
                    networkManager = getMethod(ClientConnectedToServerEvent.class,
                            new String[]{"getManager"}).invoke(event);
                    break;
            }
            // Original method name: getRemoteAddress
            address = (SocketAddress) getMethod(networkManager.getClass(),
                    new String[]{"func_74430_c", "getRemoteAddress"}).invoke(networkManager);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return address;
    }

    public static String getUnformattedText(ClientChatReceivedEvent event) {
        String msg = "";
        try {
            Object component = null;
            switch (Autotip.MC_VERSION) {
                case V1_8:
                case V1_8_8:
                case V1_8_9:
                    component = getField(ClientChatReceivedEvent.class, "message").get(event);
                    break;
                case V1_9:
                case V1_9_4:
                case V1_10:
                case V1_10_2:
                case V1_11:
                case V1_11_2:
                case V1_12:
                    component = getMethod(ClientChatReceivedEvent.class, new String[]{"getMessage"})
                            .invoke(event);
                    break;
            }
            // Original method name: getUnformattedText
            msg = (String) getMethod(component.getClass(),
                    new String[]{"getUnformattedText", "func_150260_c"})
                    .invoke(component);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return msg;
    }

    static void chatMessage(String text) {
        chatMessage(createComponent(text));
    }

    static void chatMessage(String text, String url, String hoverText) {
        chatMessage(createComponent(text, url, hoverText));
    }

    private static void chatMessage(Object component) {
        EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        try {
            switch (Autotip.MC_VERSION) {
                case V1_8:
                case V1_8_8:
                case V1_8_9:
                    // Original method name: addChatMessage
                    getMethod(
                            EntityPlayerSP.class,
                            new String[]{"func_145747_a", "addChatMessage"},
                            getClazz("net.minecraft.util.IChatComponent")
                    ).invoke(thePlayer, component);
                    break;
                case V1_9:
                case V1_9_4:
                case V1_10:
                case V1_10_2:
                    // Original method name: addChatComponentMessage
                    getMethod(
                            EntityPlayerSP.class,
                            new String[]{"func_146105_b", "addChatComponentMessage"},
                            getClazz("net.minecraft.util.text.ITextComponent")
                    ).invoke(thePlayer, component);
                    break;
                case V1_11:
                case V1_11_2:
                case V1_12:
                    // Original method name: addChatMessage / sendMessage
                    getMethod(
                            EntityPlayerSP.class,
                            new String[]{"func_145747_a", "sendMessage", "addChatMessage"},
                            getClazz("net.minecraft.util.text.ITextComponent")
                    ).invoke(thePlayer, component);
                    break;
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Object createComponent(String text) {
        try {
            switch (Autotip.MC_VERSION) {
                case V1_8:
                case V1_8_8:
                case V1_8_9:
                    return getConstructor(
                            getClazz("net.minecraft.util.ChatComponentText"),
                            String.class
                    ).newInstance(text);
                case V1_9:
                case V1_9_4:
                case V1_10:
                case V1_10_2:
                case V1_11:
                case V1_11_2:
                case V1_12:
                    return getConstructor(
                            getClazz("net.minecraft.util.text.TextComponentString"),
                            String.class
                    ).newInstance(text);
                default:
                    return null;
            }
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Don't try this at home.
    private static Object createComponent(String text, String url, String hoverText) {
        try {
            switch (Autotip.MC_VERSION) {
                case V1_8:
                case V1_8_8:
                case V1_8_9:
                    Object chatClickEvent = null;
                    Object chatHoverEvent = null;

                    if (url != null && !url.equals("")) {
                        chatClickEvent = getConstructor(
                                getClazz("net.minecraft.event.ClickEvent"),
                                getClazz("net.minecraft.event.ClickEvent$Action"),
                                String.class
                        ).newInstance(
                                getEnum(getClazz("net.minecraft.event.ClickEvent$Action"),
                                        "OPEN_URL"),
                                url
                        );
                    }

                    if (hoverText != null && !hoverText.equals("")) {
                        chatHoverEvent = getConstructor(
                                getClazz("net.minecraft.event.HoverEvent"),
                                getClazz("net.minecraft.event.HoverEvent$Action"),
                                getClazz("net.minecraft.util.IChatComponent")
                        ).newInstance(
                                getEnum(getClazz("net.minecraft.event.HoverEvent$Action"),
                                        "SHOW_TEXT"),
                                createComponent(hoverText)
                        );
                    }

                    Object chatStyle = getConstructor(
                            getClazz("net.minecraft.util.ChatStyle")
                    ).newInstance();

                    // Original method name: setChatClickEvent
                    if (url != null && !url.equals("")) {
                        getMethod(
                                getClazz("net.minecraft.util.ChatStyle"),
                                new String[]{"func_150241_a", "setChatClickEvent"},
                                getClazz("net.minecraft.event.ClickEvent")
                        ).invoke(chatStyle, chatClickEvent);
                    }

                    // Original method name: setChatHoverEvent
                    if (hoverText != null && !hoverText.equals("")) {
                        getMethod(
                                getClazz("net.minecraft.util.ChatStyle"),
                                new String[]{"func_150209_a", "setChatHoverEvent"},
                                getClazz("net.minecraft.event.HoverEvent")
                        ).invoke(chatStyle, chatHoverEvent);
                    }

                    Object chatComponent = createComponent(text);

                    // Original method name: setChatStyle
                    return getMethod(
                            getClazz("net.minecraft.util.ChatComponentText"),
                            new String[]{"func_150255_a", "setChatStyle"},
                            getClazz("net.minecraft.util.ChatStyle")
                    ).invoke(chatComponent, chatStyle);
                case V1_9:
                case V1_9_4:
                case V1_10:
                case V1_10_2:
                case V1_11:
                case V1_11_2:
                case V1_12:
                    Object clickEvent = null;
                    Object hoverEvent = null;

                    if (url != null && !url.equals("")) {
                        clickEvent = getConstructor(
                                getClazz("net.minecraft.util.text.event.ClickEvent"),
                                getClazz("net.minecraft.util.text.event.ClickEvent$Action"),
                                String.class
                        ).newInstance(
                                getEnum(getClazz("net.minecraft.util.text.event.ClickEvent$Action"),
                                        "OPEN_URL"),
                                url
                        );
                    }

                    if (hoverText != null && !hoverText.equals("")) {
                        hoverEvent = getConstructor(
                                getClazz("net.minecraft.util.text.event.HoverEvent"),
                                getClazz("net.minecraft.util.text.event.HoverEvent$Action"),
                                getClazz("net.minecraft.util.text.ITextComponent")
                        ).newInstance(
                                getEnum(getClazz("net.minecraft.util.text.event.HoverEvent$Action"),
                                        "SHOW_TEXT"),
                                createComponent(hoverText)
                        );
                    }

                    Object style = getConstructor(
                            getClazz("net.minecraft.util.text.Style")
                    ).newInstance();

                    // Original method name: setChatClickEvent
                    if (url != null && !url.equals("")) {
                        getMethod(
                                getClazz("net.minecraft.util.text.Style"),
                                new String[]{"func_150241_a", "setChatClickEvent"},
                                getClazz("net.minecraft.util.text.event.ClickEvent")
                        ).invoke(style, clickEvent);
                    }

                    // Original method name: setChatHoverEvent
                    if (hoverText != null && !hoverText.equals("")) {
                        getMethod(
                                getClazz("net.minecraft.util.text.Style"),
                                new String[]{"func_150209_a", "setChatHoverEvent"},
                                getClazz("net.minecraft.util.text.event.HoverEvent")
                        ).invoke(style, hoverEvent);
                    }

                    Object textComponent = createComponent(text);

                    // Original method name: setChatStyle
                    return getMethod(
                            getClazz("net.minecraft.util.text.TextComponentString"),
                            new String[]{"func_150255_a", "setChatStyle"},
                            getClazz("net.minecraft.util.text.Style")
                    ).invoke(textComponent, style);
                default:
                    return null;
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

}
