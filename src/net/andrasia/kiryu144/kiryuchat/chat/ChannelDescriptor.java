package net.andrasia.kiryu144.kiryuchat.chat;


import net.andrasia.kiryu144.kiryuchat.KiryuChat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.TreeMap;

public class ChannelDescriptor implements Comparable<ChannelDescriptor> {
    private static TreeMap<String, ChannelDescriptor> instances = new TreeMap<>();

    private final String name;
    private final String displayName;

    private TextComponent[] buttons = new TextComponent[ChannelButtonState.values().length];

    public ChannelDescriptor(String name, String displayName) {
        this.name = name.toLowerCase().replace(" ", "");
        this.displayName = displayName;

        for(ChannelButtonState channelButtonState : ChannelButtonState.values()){
            String path = "chat.layout.channel_button." + channelButtonState.name().toLowerCase();
            buttons[channelButtonState.ordinal()] = createButton(KiryuChat.instance.getConfig().getString(path, "§0[%s]"));
        }

        instances.put(this.name, this);
    }

    public static ChannelDescriptor valueOf(String name){
        return instances.get(name);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    private TextComponent createButton(String layout){
        TextComponent button = new TextComponent(String.format(layout, displayName));
        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(button.getText()).create()));
        button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chat select " + name));
        return button;
    }

    public TextComponent getButtonTextComponent(ChannelButtonState channelButtonState) {
        return buttons[channelButtonState.ordinal()];
    }

    @Override
    public int compareTo(ChannelDescriptor o) {
        return name.compareTo(o.name);
    }
}
