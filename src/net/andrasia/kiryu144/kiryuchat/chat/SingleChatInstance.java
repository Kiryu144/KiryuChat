package net.andrasia.kiryu144.kiryuchat.chat;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class SingleChatInstance {
    private final List<TextComponent> view = new ArrayList<>();
    private TextComponent message = new TextComponent("");
    private boolean isUnread = false;

    public SingleChatInstance() {
        for(int i = 0; i < 48; ++i){
            view.add(new TextComponent(" "));
        }
    }

    public void write(TextComponent message){
        view.remove(0);
        view.add(message);
    }

    public void createMessage() {
        message = new TextComponent("");
        for(TextComponent line : view){
            message.addExtra(line);
            message.addExtra("\n");
        }
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    public TextComponent getMessage() {
        return message;
    }

}
