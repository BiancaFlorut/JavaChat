package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        String line = null;
        while (true) {
            try {
                line = reader.readLine();
                if (line != null) return line;
            } catch (IOException e) {
                writeMessage("An error occurred while trying to enter text. Try again.");
            }
        }
    }

    public static int readInt() {
        while (true) {
            try {
                String line = readString();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                writeMessage("An error occurred while trying to enter a number. Try again.");
            }
        }
    }
}
