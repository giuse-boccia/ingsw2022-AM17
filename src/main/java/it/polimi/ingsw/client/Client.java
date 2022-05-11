package it.polimi.ingsw.client;

import it.polimi.ingsw.Settings;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.game_objects.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Client {
    private static NetworkClient nc;
    private String username;
    private String[] characters;

    /**
     * Starts correctly the {@code CLI} or the {@code GUI} accordingly to the user choice
     *
     * @param args An array of {@code String} containing the choice of the user and the input parameters to establish a connection
     */
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

    /**
     * Asks the user to input a value to play an {@code Assistant}
     *
     * @return the value chosen by the user
     */
    public abstract int getAssistantValue() throws IOException;

    /**
     * Shows the list of actions the user can choose from
     *
     * @param actions the list of actions the user can choose from
     */
    public abstract void showPossibleActions(List<String> actions);

    /**
     * Makes the user choose an action from a list by inputting a number between 1 and bound
     *
     * @param bound the maximum int the user can input to choose an action
     * @return the index of the chose action
     */
    public abstract int chooseAction(int bound) throws IOException;

    /**
     * Asks the user to input a color
     *
     * @return the {@code Color} chosen by the user
     */
    public abstract Color askStudentColor() throws IOException;

    /**
     * Asks the user to input the index of an {@code Island}
     *
     * @return the index of the chosen {@code Island}
     */
    public abstract int askIslandIndex() throws IOException;

    /**
     * Asks the user to input the number of moves they want to make MotherNature do
     *
     * @return the number of moves chosen by the user
     */
    public abstract int askNumStepsOfMotherNature() throws IOException;

    /**
     * Asks the user to input the index of an {@code Cloud}
     *
     * @return the index of the chosen {@code Cloud}
     */
    public abstract int askCloudIndex() throws IOException;

    /**
     * Asks the user to input the index of an {@code Character}
     *
     * @return the index of the chosen {@code Character}
     */
    public abstract int askCharacterIndex() throws IOException;

    /**
     * Shows the list of {@code Characters} present in the {@code Game}, each one with an index starting from 1
     */
    public abstract void showAllCharactersWithIndex() throws IOException;

    /**
     * Asks the user to choose an {@code ArrayList} of colors of students to be swapped
     *
     * @param maxBound      the maximum number of students which can be swapped
     * @param secondElement the second place where to swap the students from/to
     * @return the {@code ArrayList} of colors of students to be swapped
     */
    public abstract ArrayList<Color> askColorListForSwapCharacters(int maxBound, String secondElement) throws IOException;

    /**
     * Ends the {@code Game} showing a message
     *
     * @param message the message to be shown
     */
    public abstract void endGame(String message);

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String[] getCharacters() {
        return characters;
    }

    public void setCharacters(String[] characters) {
        this.characters = characters;
    }
}
