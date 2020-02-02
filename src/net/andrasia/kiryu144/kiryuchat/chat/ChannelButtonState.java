package net.andrasia.kiryu144.kiryuchat.chat;

public enum ChannelButtonState {
    NORMAL(0),
    SELECTED(1),
    UNREAD(2);

    private final int id;

    private ChannelButtonState(int id){
        this.id = id;
    }
}
