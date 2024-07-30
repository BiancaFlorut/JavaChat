package client;

import main.*;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public class SocketThread extends Thread {

        public void run() {
            String serverAddress = getServerAddress();
            int portNumber = getServerPort();
            try (Socket socket = new Socket(serverAddress, portNumber)){
                connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            } catch (IOException e) {
                ConsoleHelper.writeMessage("An error occurred while opening the socket connection." + e.getMessage());
                e.printStackTrace();
                notifyConnectionStatusChanged(false);
            } catch (ClassNotFoundException e) {
                ConsoleHelper.writeMessage("An error occurred in the client connection protocol.");
                notifyConnectionStatusChanged(false);
            }
        }

        protected void processIncomingMessage(MessageData message) {
            ConsoleHelper.writeMessage(message.toString());
        }

        protected void informAboutAddingNewUse(String userName) {
            ConsoleHelper.writeMessage(userName + " has joined the chat.");
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + " has left the Chat.");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if( message != null )
                    if (message.getType() == MessageType.NAME_REQUEST) {
                        String userName = getUserName();
                        connection.send(new Message(MessageType.USER_NAME, new MessageData(userName)));
                    } else if (message.getType() == MessageType.NAME_ACCEPTED) {
                        notifyConnectionStatusChanged(true);
                        break;
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    processIncomingMessage(message.getData());
                } else if(message.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUse(message.getData().getUserName());
                } else if (message.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(message.getData().getUserName());
                } else throw new IOException("Unexpected MessageType");
            }
        }
    }

    protected Connection connection;
    private volatile boolean clientConnected = false;
    private String userName;

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
        Message message = new Message(MessageType.TEXT, new MessageData(userName, text));
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
        } else ConsoleHelper.writeMessage("An error occurred while working with the client.");
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
