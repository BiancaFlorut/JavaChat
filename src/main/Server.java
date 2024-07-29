package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    private static class Handler extends Thread {
        private Socket socket;
        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            ConsoleHelper.writeMessage("A new connection with " + socket.getRemoteSocketAddress() +
                    " has been established");
            try (Connection connection = new Connection(socket)){
                String clientName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, clientName));
                notifyUsers(connection, clientName);
                serverMainLoop(connection, clientName);
            } catch (Exception e) {
                ConsoleHelper.writeMessage("Error communicating with " + socket.getRemoteSocketAddress());
            }

        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                Message nameRequest = new Message(MessageType.NAME_REQUEST);
                connection.send(nameRequest);
                Message response = connection.receive();
                if (response.getType() != MessageType.USER_NAME)
                    ConsoleHelper.writeMessage("main.Message received from " + socket.getRemoteSocketAddress() +
                            ". The message type does not match the protocol.");
                if (response.getData().isEmpty())
                    ConsoleHelper.writeMessage("There was an attempt to connect to the server using an empty name from  " +
                            socket.getRemoteSocketAddress());
                if (connectionMap.containsKey(response.getData()))
                    ConsoleHelper.writeMessage("There was an attempt to connect to the server using a name that is already being used from: " +
                            socket.getRemoteSocketAddress());

                connectionMap.put(response.getData(), connection);
                Message success = new Message(MessageType.NAME_ACCEPTED);
                connection.send(success);
                return  response.getData();

            }
        }

        private void notifyUsers(Connection connection, String userName) throws IOException{
            for (Map.Entry<String, Connection> entry: connectionMap.entrySet()) {
                Message newUser = new Message(MessageType.USER_ADDED, entry.getKey());
                if (!entry.getKey().equals(userName)) connection.send(newUser);
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message clientMessage = connection.receive();
                if (clientMessage.getType() == MessageType.TEXT) {
                    Message message = new Message(MessageType.TEXT, userName + ": " + clientMessage.getData());
                    sendBroadcastMessage(message);
                } else ConsoleHelper.writeMessage("The send message is not a text message.");
            }
        }
    }

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Enter the server port number: ");
        int port = ConsoleHelper.readInt();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("main.Server is working on port: " + port);
            while (true) {
                Socket client = serverSocket.accept();
                Handler handler = new Handler(client);
                handler.start();
            }
        } catch (Exception e) {
            ConsoleHelper.writeMessage("An error has occurred. " + e.getMessage());
        }
    }

    public static void sendBroadcastMessage(Message message) {
        for (Map.Entry<String, Connection> entry: connectionMap.entrySet()) {
            try {
                entry.getValue().send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Unable to sent message to: " + entry.getKey());
            }
        }
    }
}
