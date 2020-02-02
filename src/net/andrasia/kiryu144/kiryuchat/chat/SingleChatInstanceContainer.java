package net.andrasia.kiryu144.kiryuchat.chat;

import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.Validate;

import java.util.TreeMap;
import java.util.UUID;

public class SingleChatInstanceContainer implements Comparable<SingleChatInstanceContainer> {
    private TreeMap<ChannelDescriptor, SingleChatInstance> chats = new TreeMap<>();
    private ChannelDescriptor selected;
    private UUID owner;
    private TextComponent channelButtons;

    public SingleChatInstanceContainer(UUID owner) {
        this.owner = owner;
    }

    public void updateChannelButtons() {
        channelButtons = new TextComponent();
        for(ChannelDescriptor channelDescriptor : chats.keySet()){
            ChannelButtonState channelButtonState = ChannelButtonState.NORMAL;
            if(channelDescriptor == selected){
                channelButtonState = ChannelButtonState.SELECTED;
            }else if(chats.get(channelDescriptor).isUnread()){
                channelButtonState = ChannelButtonState.UNREAD;
            }
            channelButtons.addExtra(channelDescriptor.getButtonTextComponent(channelButtonState));
            channelButtons.addExtra(" ");
        }
    }

    public TextComponent getChannelButtons() {
        return channelButtons;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setSelected(ChannelDescriptor selected){
        Validate.isTrue(chats.containsKey(selected));
        this.selected = selected;
        updateChannelButtons();
    }

    public ChannelDescriptor getSelectedChannel(){
        return selected;
    }

    public SingleChatInstance getSelectedChat(){
        Validate.notNull(selected, "No channel was selected");
        return chats.get(selected);
    }

    public void addChannel(ChannelDescriptor channelDescriptor){
        chats.putIfAbsent(channelDescriptor, new SingleChatInstance());
    }

    public void sendMessage(ChannelDescriptor channel, TextComponent message){
        SingleChatInstance singleChatInstance = chats.get(channel);
        singleChatInstance.write(message);
    }

    public SingleChatInstance getSingleChatInstance(ChannelDescriptor channelDescriptor){
        return chats.get(channelDescriptor);
    }

    @Override
    public int compareTo(SingleChatInstanceContainer o) {
        return owner.compareTo(o.owner);
    }
}
