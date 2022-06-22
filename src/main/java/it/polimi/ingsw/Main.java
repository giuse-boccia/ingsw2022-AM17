package it.polimi.ingsw;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.server.Server;

import java.util.Arrays;
import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        if (args.length > 3) {
            System.out.println(Messages.USAGE);
            System.exit(-1);
        }

        if (args.length == 0) {
            Client.main(new String[]{"gui"});
        } else {
            // Args that will be passed to the main() method of Server, CLI or GUI
            String[] config = Arrays.copyOfRange(args, 1, args.length);
            MessageResourceBundle.initializeBundle("en");
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "server" -> Server.main(config);
                case "cli", "gui" -> Client.main(args);
                default -> {
                    System.out.println(Messages.USAGE);
                    System.exit(-1);
                }
            }
        }

    }



}
