package net.andrasia.kiryu144.kiryuchat;

import net.andrasia.kiryu144.kiryuchat.chat.ChannelDescriptor;
import net.andrasia.kiryu144.kiryuchat.chat.ChatConfig;
import net.andrasia.kiryu144.kiryuchat.chat.PlayerChatManager;
import net.andrasia.kiryu144.kiryuchat.chat.SingleChatInstanceContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class KiryuChat extends JavaPlugin implements Listener {
    public static KiryuChat instance;
    public static PlayerChatManager playerChatManager;
    public static ChatConfig chatConfig;

    @Override
    public void onEnable() {
        instance = this;
        chatConfig = new ChatConfig();
        playerChatManager = new PlayerChatManager();

        chatConfig.load();

        for(Player player : Bukkit.getOnlinePlayers()){
            playerChatManager.joinPlayer(player);
        }

        Bukkit.getPluginManager().registerEvents(playerChatManager, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player && cmd.getName().equalsIgnoreCase("chat")){
            if(args.length == 2 && args[0].equalsIgnoreCase("select")){
                SingleChatInstanceContainer container = playerChatManager.getSingleChatInstanceContainer(((Player) sender).getUniqueId());
                container.setSelected(ChannelDescriptor.valueOf(args[1].toLowerCase()));
                container.getSelectedChat().setUnread(false);
                container.updateChannelButtons();
                playerChatManager.render(container.getOwner());
                return true;
            }
        }
        return false;
    }
}
