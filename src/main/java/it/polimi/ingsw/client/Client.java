package it.polimi.ingsw.client;

import it.polimi.ingsw.Settings;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_state.CharacterState;
import it.polimi.ingsw.model.game_state.GameState;

import java.io.IOException;
import java.util.List;

public abstract class Client {
    private static NetworkClient nc;
    private String username;
    private List<CharacterState> characters;
    private Observer currentObserver;

    /**
     * Starts correctly the {@code CLI} or the {@code GUI} accordingly to the user choice
     *
     * @param args An array of {@code String} containing the choice of the user and the input parameters to establish a connection
     */
    public static void main(String[] args) {
        Client client;
        String serverAddress = null;
        int serverPort = -1;

        if (args[0].equalsIgnoreCase("cli")) {
            client = new CLI();
        } else {
            client = new GUI();
            new Thread(() -> GuiView.main(new String[]{})).start();
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
     */
    public abstract void askUsername() throws IOException;

    /**
     * Asks the player to input the number of players
     */
    public abstract void askNumPlayersAndExpertMode() throws IOException;

    /**
     * Shows the current state of the lobby
     *
     * @param gameLobby the {@code GameLobby} object containing the list of players
     */
    public abstract void showCurrentLobby(GameLobby gameLobby);

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
     */
    public abstract void getAssistantValue() throws IOException;

    /**
     * Shows the list of actions the user can choose from
     *
     * @param actions the list of actions the user can choose from
     */
    public abstract void showPossibleActions(List<String> actions);

    /**
     * Makes the user choose an action from a list by inputting a number between 1 and bound
     *
     * @param actions the maximum int the user can input to choose an action
     */
    public abstract void chooseAction(List<String> actions) throws IOException;

    /**
     * Asks the user to input a color
     */
    public abstract void askMoveStudentToDining() throws IOException;

    /**
     * Asks the user to input the number of moves they want to make MotherNature do
     */
    public abstract void askNumStepsOfMotherNature() throws IOException;

    /**
     * Asks the user to input the index of an {@code Cloud}
     */
    public abstract void askCloudIndex() throws IOException;

    /**
     * Asks the user to input the index of an {@code Character}
     */
    public abstract void askCharacterIndex() throws IOException;

    /**
     * Shows the list of {@code Characters} present in the {@code Game}, each one with an index starting from 1
     */
    public abstract void showAllCharactersWithIndex() throws IOException;

    /**
     * Asks the user to choose an {@code ArrayList} of colors of students to be swapped
     *
     * @param maxBound      the maximum number of students which can be swapped
     * @param secondElement the second place where to swap the students from/to
     * @param characterName the name of the selected {@code Character}
     */
    public abstract void askColorListForSwapCharacters(int maxBound, String secondElement, CharacterName characterName) throws IOException;

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

    public List<CharacterState> getCharacters() {
        return characters;
    }

    public void setCharacters(List<CharacterState> characters) {
        this.characters = characters;
    }

    public Observer getCurrentObserver() {
        return currentObserver;
    }

    public void setCurrentObserver(Observer currentObserver) {
        this.currentObserver = currentObserver;
    }

    public abstract void updateGameState(GameState gameState);

    public abstract void askMoveStudentToIsland() throws IOException;

    public abstract void askToMoveOneStudentFromCard(boolean toIsland) throws IOException;

    public abstract void askIslandIndexForCharacter(CharacterName characterName) throws IOException;

    public abstract void pickColorForPassive(CharacterName characterName) throws IOException;
}
