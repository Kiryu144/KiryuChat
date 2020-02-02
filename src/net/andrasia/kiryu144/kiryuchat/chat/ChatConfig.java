package net.andrasia.kiryu144.kiryuchat.chat;

import net.andrasia.kiryu144.kiryuchat.KiryuChat;

public class ChatConfig {

    public ChatConfig() {

    }

    public void load() {
        KiryuChat.instance.saveResource("config.yml", false);
        KiryuChat.instance.reloadConfig();

        // Load all channels
        for(String channelName : KiryuChat.instance.getConfig().getConfigurationSection("chat.channel").getKeys(false)){
            String channelDisplay = KiryuChat.instance.getConfig().getString("chat.channel." + channelName + ".display");
            KiryuChat.playerChatManager.addDefaultChannel(new ChannelDescriptor(channelName, channelDisplay));
        }
    }

}
