package it.polimi.ingsw.server;

import com.google.gson.Gson;
import it.polimi.ingsw.Settings;

import java.io.IOException;
import java.util.Scanner;

public class Server {

    private static int port;

    public static void main(String[] args) {
        if (args.length == 0) {
            try {
                Settings settings = Settings.readPrefsFromFile();
                port = settings.getPort();
            } catch (IOException e) {
                gracefulTermination("File settings.json not found. Please check documentation");
            } catch (NumberFormatException e) {
                gracefulTermination("Invalid server_port argument in settings.json");
            }
        } else {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                gracefulTermination("Invalid server_port argument in settings.json");
            }
        }

        if (port < 1024 || port > 65535) {
            gracefulTermination("Invalid server_port argument. The port number has to be between 1024 and 65535");
        }

        // Now starts ServerSocket
    }

    private static void gracefulTermination(String message) {
        System.out.println(message);
        System.out.println("Application will now close...");
        System.exit(-1);
    }

}
