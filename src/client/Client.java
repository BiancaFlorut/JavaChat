package client;

import main.Connection;
import main.ConsoleHelper;
import main.Message;
import main.MessageType;

import java.io.IOException;

public class Client {
    public class SocketThread extends Thread {

    }
    protected Connection connection;
    private volatile boolean clientConnected = false;

    protected String getServerAddress() {
        ConsoleHelper.writeMessage("Type the server address: ");
        return ConsoleHelper.readString();
    }

    protected int getServerPort() {
        ConsoleHelper.writeMessage("Type the server port number: ");
        return ConsoleHelper.readInt();
    }

    protected String getUserName() {
        ConsoleHelper.writeMessage("Please enter your username: ");
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        Message message = new Message(MessageType.TEXT, text);
        try {
            connection.send(message);
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Unable to sent message: " + connection.getRemoteSocketAddress());
            clientConnected = false;
        }
    }

    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e){
            ConsoleHelper.writeMessage("An error has occurred by waiting for connection.");
            System.exit(0);
        }
        if (clientConnected) {
            ConsoleHelper.writeMessage("Connection established. To exit, enter 'exit'.");
        } ConsoleHelper.writeMessage("An error occurred while working with the client.");
        while (clientConnected) {
            String line = ConsoleHelper.readString();
            if (line.equals("exit")) break;
            if (shouldSendTextFromConsole())
                sendTextMessage(line);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
