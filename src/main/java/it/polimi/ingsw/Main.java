package it.polimi.ingsw;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.server.Server;

import java.util.Arrays;
import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        MessageResourceBundle.initializeBundle("en");
        if (args.length > 3) {
            System.out.println(MessageResourceBundle.getMessage("wrong_usage"));
            System.exit(-1);
        }

        if (args.length == 0) {
            Client.main(new String[]{"gui"});
        } else {
            // Args that will be passed to the main() method of Server, CLI or GUI
            String[] config = Arrays.copyOfRange(args, 1, args.length);
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "server" -> Server.main(config);
                case "cli", "gui" -> Client.main(args);
                default -> {
                    System.out.println(MessageResourceBundle.getMessage("wrong_usage"));
                    System.exit(-1);
                }
            }
        }

    }



}
