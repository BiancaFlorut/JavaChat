package main;

import java.io.Serializable;

public class Message implements Serializable {
    private final MessageType type;
    private final MessageData data;

    public Message(MessageType messageType) {
        this.type = messageType;
        this.data = null;
    }

    public Message(MessageType type, MessageData data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public MessageData getData() {
        return data;
    }

    @Override
    public String toString() {
        if (data != null)
        return type.toString() + " " + data.toString();
        else return type.toString();
    }
}
