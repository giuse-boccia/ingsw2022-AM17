package it.polimi.ingsw;

import com.google.gson.Gson;
import it.polimi.ingsw.client.CLI;
import it.polimi.ingsw.client.GUI;
import it.polimi.ingsw.server.Server;

import java.util.Arrays;
import java.util.Locale;

public class Main {

    private static int port;
    private static String address;

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            System.out.println("Usage: java -jar eriantys.jar [SERVER | CLI | GUI] [<PORT_NUMBER>] [<HOST_ADDRESS>]");
            System.exit(-1);
        }

        // Args that will be passed to the main() method of Server, CLI or GUI
        String[] config = Arrays.copyOfRange(args, 1, args.length);

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "server" -> Server.main(config);
            case "cli" -> CLI.main(config);
            case "gui" -> GUI.main(config);
            default -> {
                System.out.println("Usage: java -jar eriantys.jar [SERVER | CLI | GUI] [<PORT_NUMBER>] [<HOST_ADDRESS>]");
                System.exit(-1);
            }
        }

    }



}
