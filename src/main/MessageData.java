package main;

import java.io.Serializable;

public class MessageData implements Serializable {
    private final String userName;
    private final String text;

    public MessageData(String userName, String text) {
        this.userName = userName;
        this.text = text;
    }

    public MessageData(String userName) {
        this.userName = userName;
        this.text = null;
    }

    public String getUserName() {
        return userName;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return userName + ":" + text;
    }
}
