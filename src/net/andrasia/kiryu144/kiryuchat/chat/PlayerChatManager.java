package net.andrasia.kiryu144.kiryuchat.chat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.andrasia.kiryu144.kiryuchat.KiryuChat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

public class PlayerChatManager implements Listener {
    private TreeMap<SingleChatInstanceContainer, SingleChatInstanceContainer> chats = new TreeMap<>();
    private TreeSet<ChannelDescriptor> defaultChannels = new TreeSet<>();

    public PlayerChatManager() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
            new PacketAdapter(KiryuChat.instance, ListenerPriority.NORMAL, PacketType.Play.Server.CHAT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    EnumWrappers.ChatType chatType = event.getPacket().getChatTypes().read(0);
                    if(chatType.equals(EnumWrappers.ChatType.SYSTEM)){
                        event.setCancelled(true);
                        TextComponent system = new TextComponent("System ");
                        system.setColor(ChatColor.DARK_GREEN);

                        try {
                            BaseComponent[] messages = ComponentSerializer.parse(event.getPacket().getChatComponents().getValues().get(0).getJson());
                            for(BaseComponent component : messages){
                                component.setColor(ChatColor.GREEN);
                                system.addExtra(component);
                            }
                        }catch(Throwable exception) {
                            system.addExtra(String.format("{Exception @ %s}", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now())));
                            exception.printStackTrace();
                        }

                        KiryuChat.playerChatManager.sendMessage(event.getPlayer().getUniqueId(), system);
                    }
                }
            }
        );
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        joinPlayer(event.getPlayer());
    }

    public void joinPlayer(Player player){
        SingleChatInstanceContainer instance = new SingleChatInstanceContainer(player.getUniqueId());
        for(ChannelDescriptor channelDescriptor : defaultChannels){
            instance.addChannel(channelDescriptor);
        }
        instance.setSelected(defaultChannels.first());
        instance.updateChannelButtons();
        chats.put(instance, instance);
        render(player.getUniqueId());
    }

    @EventHandler
    private void onPlayerWrite(AsyncPlayerChatEvent event){
        event.setCancelled(true);

        TextComponent message = new TextComponent(String.format(KiryuChat.instance.getConfig().getString("chat.layout.messages.player", "%s %s"), event.getPlayer().getName(), event.getMessage()));

        ChannelDescriptor channel = getSingleChatInstanceContainer(event.getPlayer().getUniqueId()).getSelectedChannel();
        for(Player recipient : event.getRecipients()){
            sendMessage(recipient.getUniqueId(), channel, message);
        }
    }

    public void addDefaultChannel(ChannelDescriptor channelDescriptor){
        defaultChannels.add(channelDescriptor);
    }

    public void sendMessage(UUID uuid, ChannelDescriptor channelDescriptor, TextComponent message){
        SingleChatInstanceContainer singleChatInstanceContainer = chats.get(new SingleChatInstanceContainer(uuid));
        if(singleChatInstanceContainer != null){
            singleChatInstanceContainer.sendMessage(channelDescriptor, message);
            if(singleChatInstanceContainer.getSelectedChannel().equals(channelDescriptor)){
                render(uuid);
            }else{
                SingleChatInstance instance = singleChatInstanceContainer.getSingleChatInstance(channelDescriptor);
                if(instance != null){
                    if(!instance.isUnread()){
                        instance.setUnread(true);
                        getSingleChatInstanceContainer(uuid).updateChannelButtons();
                        render(uuid);
                    }

                }
            }
        }
    }

    public void sendMessage(UUID uuid, TextComponent message){
        sendMessage(uuid, getSingleChatInstanceContainer(uuid).getSelectedChannel(), message);
    }

    public SingleChatInstanceContainer getSingleChatInstanceContainer(UUID uuid){
        return chats.get(new SingleChatInstanceContainer(uuid));
    }

    public void render(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player != null){
            SingleChatInstanceContainer singleChatInstanceContainer = chats.get(new SingleChatInstanceContainer(uuid));
            SingleChatInstance singleChatInstance = singleChatInstanceContainer.getSelectedChat();
            singleChatInstance.createMessage();

            TextComponent textComponent = new TextComponent(singleChatInstance.getMessage());
            textComponent.addExtra(singleChatInstanceContainer.getChannelButtons());

            player.spigot().sendMessage(ChatMessageType.CHAT, textComponent);
        }
    }
}
