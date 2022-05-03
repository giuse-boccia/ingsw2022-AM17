package it.polimi.ingsw.client;

import it.polimi.ingsw.Settings;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.game_objects.Color;

import java.io.IOException;

public abstract class Client {
    private static NetworkClient nc;

    public static void main(String[] args) {
        Client client;
        String serverAddress = null;
        int serverPort = -1;

        if (args[0].equals("cli")) {
            client = new CLI();
        } else {
            client = new GUI();
        }
        if (args.length < 3) {
            try {
                Settings settings = Settings.readPrefsFromFile();
                if (settings.getAddress() == null) {
                    client.gracefulTermination("Invalid server_address argument in settings.json");
                }
                serverPort = settings.getPort();
                serverAddress = settings.getAddress();
            } catch (IOException e) {
                client.gracefulTermination("File settings.json not found. Please check documentation");
            } catch (NumberFormatException e) {
                client.gracefulTermination("Invalid server_port argument in settings.json");
            }
        } else {
            try {
                serverPort = Integer.parseInt(args[1]);
                serverAddress = args[2];
            } catch (NumberFormatException e) {
                client.gracefulTermination("Invalid server_port argument in settings.json");
            }
        }

        if (serverPort < 1024 || serverPort > 65535) {
            client.gracefulTermination("Invalid server_port argument. The port number has to be between 1024 and 65535");
        }

        nc = new NetworkClient(client, serverAddress, serverPort);
        nc.connectToServer();
        nc.start();
    }

    /**
     * Asks the player to input a username
     *
     * @return the username string chosen by the player
     */
    public abstract String askUsername() throws IOException;

    /**
     * Asks the player to input the number of players
     *
     * @return an integer from 2 to 4 indicating the desired number of player
     */
    public abstract int askNumPlayers() throws IOException;

    /**
     * Asks the player to choose whether to play in expert mode or not
     *
     * @return true if the player wants to play in expert mode, false otherwise
     */
    public abstract boolean askExpertMode() throws IOException;

    /**
     * Shows the current state of the lobby
     *
     * @param gameLobby the {@code GameLobby} object containing the list of players
     */
    public abstract void showCurrentLobby(GameLobby gameLobby);

    /**
     * Asks the player to pick a color
     *
     * @return the picked color
     */
    public abstract Color pickColor();

    /**
     * Closes the connection printing the provided message
     *
     * @param message the {@code String} to be printed
     */
    public abstract void gracefulTermination(String message);

    /**
     * Shows the message to the user
     *
     * @param message the message to be shown
     */
    public abstract void showMessage(String message);
}
