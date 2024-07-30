package client;

import main.ConsoleHelper;

import java.io.IOException;

public class BotClient extends Client{
    public class BotSocketThread extends Client.SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Hi, I'm a bot. I understand the following commands: date, day, month, year, time, hour, minutes, seconds.");
            super.clientMainLoop();
        }

//        @Override
//        protected void processIncomingMessage(String message) {
//            ConsoleHelper.writeMessage("Received message: " + message);
//
//        }
    }

    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return "date_bot_" + Math.round(Math.random() * 99);
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
}
