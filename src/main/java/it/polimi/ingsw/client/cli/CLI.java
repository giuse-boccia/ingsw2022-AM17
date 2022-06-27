package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.utils.RandomNicknameGenerator;
import it.polimi.ingsw.utils.constants.Constants;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.server.game_state.GameState;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CLI extends Client {
    private final BufferedReader stdIn;

    public CLI() {
        stdIn = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void getAssistantValue() throws IOException {
        int value = askForInteger(1, 10, MessageResourceBundle.getMessage("ask_assistant"), "Assistant value");
        getCurrentObserverHandler().notifyPlayAssistantObservers(value);
    }

    @Override
    public void askUsername() throws IOException {
        System.out.print(MessageResourceBundle.getMessage("ask_username"));
        String username = stdIn.readLine();
        if (username.isEmpty()) {
            username = RandomNicknameGenerator.getRandomNickname();
        }
        setTmpUsername(username);
        getCurrentObserverHandler().notifyAllUsernameObservers(username);
    }

    @Override
    public void askCreateOrLoad() throws IOException {
        String res;

        do {
            System.out.println(MessageResourceBundle.getMessage("no_game_running"));
            System.out.println(MessageResourceBundle.getMessage("create_game_option"));
            System.out.println(MessageResourceBundle.getMessage("load_game_option"));
            System.out.print("> ");
            res = stdIn.readLine();
        } while (!res.equals("1") && !res.equals("2"));

        if (res.equals("1")) {
            askNumPlayersAndExpertMode();
        }
        if (res.equals("2")) {
            getCurrentObserverHandler().notifyAllLoadGameObservers();
        }
    }

    @Override
    public void askNumPlayersAndExpertMode() throws IOException {
        int numPlayers = askForInteger(Constants.MIN_PLAYERS, Constants.MAX_PLAYERS, MessageResourceBundle.getMessage("ask_num_players"), "Number of players");
        boolean isGameExpert = askExpertMode();
        getCurrentObserverHandler().notifyAllGameParametersObservers(numPlayers, isGameExpert);
    }

    @Override
    public void showCurrentLobby(GameLobby lobby) {
        if (lobby.isFromSavedGame()) {
            printSavedGameLobby(lobby);
        } else {
            printNewGameLobby(lobby);
        }
    }

    private void printSavedGameLobby(GameLobby lobby) {
        printBlueLine();
        String message = MessageResourceBundle.getMessage("game_title") + lobby.getPlayers().size();
        message += "/" + lobby.getNumPlayers();
        message += " " + MessageResourceBundle.getMessage("players");
        message += MessageResourceBundle.getMessage("expert_mode") +
                (!lobby.isExpert() ? MessageResourceBundle.getMessage("not_with_space") : "")
                + MessageResourceBundle.getMessage("active");
        System.out.println(message);

        List<String> ready = lobby.getPlayers();
        for (String name : lobby.getPlayersFromSavedGame()) {
            String playerString = ready.contains(name) ?
                    MessageResourceBundle.getMessage("ready") : MessageResourceBundle.getMessage("waiting");
            playerString += "  " + name;
            System.out.println(playerString);
        }
        System.out.println();
    }

    private void printNewGameLobby(GameLobby lobby) {
        printBlueLine();
        String message = MessageResourceBundle.getMessage("game_title") + lobby.getPlayers().size();
        if (lobby.getNumPlayers() != -1) {
            message += "/" + lobby.getNumPlayers();
        }
        message += " " + MessageResourceBundle.getMessage("players");
        if (lobby.getNumPlayers() != -1) {
            message += MessageResourceBundle.getMessage("expert_mode")
                    + (!lobby.isExpert() ? MessageResourceBundle.getMessage("not_with_space") : "")
                    + MessageResourceBundle.getMessage("active");
        }
        System.out.println(message);
        for (String name : lobby.getPlayers()) {
            System.out.println("  - " + name);
        }
        System.out.println();
    }

    @Override
    public void gracefulTermination(String message) {
        printBlueLine();
        System.out.println(message);
        System.out.println(MessageResourceBundle.getMessage("graceful_term"));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(-1);
    }


    @Override
    public void chooseAction(List<String> actions) throws IOException {
        int res;
        int size = actions.size();
        do {
            System.out.print(MessageResourceBundle.getMessage("select_number"));
            String string = stdIn.readLine();
            try {
                res = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                res = -1;       // remains in the do-while cycle
                System.out.println(MessageResourceBundle.getMessage("action_must_be_number"));
            }

        } while (res < 1 || res > size);

        getCurrentObserverHandler().notifyActionChoiceObservers(actions.get(res - 1));
    }

    @Override
    public void askIslandIndexForCharacter(CharacterName characterName) throws IOException {
        int islandIndex = askForInteger(1, Constants.MAX_ISLANDS, MessageResourceBundle.getMessage("ask_island"), "Island index");
        getCurrentObserverHandler().notifyPlayCharacterObservers(characterName, null, islandIndex - 1, null, null);
    }

    @Override
    public void pickColorForPassive(CharacterName characterName) throws IOException {
        Color color = askForColor();
        getCurrentObserverHandler().notifyPlayCharacterObservers(characterName, color, null, null, null);
    }

    @Override
    public void askToMoveOneStudentFromCard(boolean toIsland) throws IOException {
        Color color = askForColor();
        Integer islandIndex = null;
        CharacterName characterName = CharacterName.move1FromCardToDining;
        if (toIsland) {
            islandIndex = askForInteger(1, 12, MessageResourceBundle.getMessage("ask_island"), "Island index")
                    - 1;
            characterName = CharacterName.move1FromCardToIsland;
        }
        getCurrentObserverHandler().notifyPlayCharacterObservers(characterName, null, islandIndex, List.of(color), null);
    }

    @Override
    public void askMoveStudentToIsland() throws IOException {
        int island = askForInteger(1, Constants.MAX_ISLANDS, MessageResourceBundle.getMessage("ask_island"), "Island index");
        Color color = askForColor();
        getCurrentObserverHandler().notifyMoveStudentObservers(color, island - 1);
    }

    @Override
    public void askCharacterIndex() throws IOException {
        int index = askForInteger(1, Constants.NUM_CHARACTERS, MessageResourceBundle.getMessage("ask_character"), "Character index");
        getCurrentObserverHandler().notifyCharacterChoiceObservers(getCharacters().get(index - 1).getCharacterName());
    }

    @Override
    public void showAllCharactersWithIndex() {
        for (int i = 0; i < getCharacters().size(); i++) {
            System.out.println((i + 1) + ". " + getCharacters().get(i).getCharacterName());
        }
    }

    @Override
    public void endGame(String message) {
        showMessage(message);
        System.exit(0);
    }

    @Override
    public void playCharacterWithoutArguments(CharacterName characterName) {
        getCurrentObserverHandler().notifyPlayCharacterObservers(characterName, null, null, null, null);
    }

    @Override
    public void askColorListForSwapCharacters(int maxBound, String secondElement, CharacterName characterName) throws IOException {
        int size = askForInteger(0, maxBound, MessageResourceBundle.getMessage("ask_num_students"), "Number of students");
        System.out.println("Select " + size + " students from your entrance");
        ArrayList<Color> srcColors = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Color toAdd = askForColor();
            srcColors.add(toAdd);
        }
        ArrayList<Color> dstColors = new ArrayList<>();
        System.out.println("Select " + size + " students from " + secondElement);
        for (int i = 0; i < size; i++) {
            Color toAdd = askForColor();
            dstColors.add(toAdd);
        }
        getCurrentObserverHandler().notifyPlayCharacterObservers(characterName, null, null, srcColors, dstColors);
    }

    @Override
    public void askMoveStudentToDining() throws IOException {
        Color color = askForColor();
        getCurrentObserverHandler().notifyMoveStudentObservers(color, null);
    }

    @Override
    public void askNumStepsOfMotherNature() throws IOException {
        int steps = askForInteger(0, Constants.MAX_MN_STEPS, MessageResourceBundle.getMessage("ask_mn"), "Number of steps");
        getCurrentObserverHandler().notifyMoveMNObservers(steps);
    }

    @Override
    public void askCloudIndex() throws IOException {
        int cloud = askForInteger(1, Constants.MAX_CLOUDS, MessageResourceBundle.getMessage("ask_cloud"), "Cloud index");
        getCurrentObserverHandler().notifyChooseCloudObservers(cloud - 1);
    }

    @Override
    public void showPossibleActions(List<String> actions) {
        showMessage(MessageResourceBundle.getMessage("possible_actions"));
        for (int i = 0; i < actions.size(); i++) {
            System.out.println((i + 1) + ". " + actions.get(i));
        }
    }

    @Override
    public void showMessage(String message) {
        printBlueLine();
        System.out.println(message);
    }

    @Override
    public void showWarningMessage(String message) {
        showMessage(message);
    }

    /**
     * Prints a line containing only "-" characters
     */
    private void printBlueLine() {
        System.out.println(CliView.BLUE_LINE);
    }

    @Override
    public void updateGameState(GameState gameState) {
        printBlueLine();
        CliView.printGameState(gameState, getUsername());
    }

    /**
     * Asks the user to input an integer between lowerBound and upperBound
     *
     * @param lowerBound                  the minimum value which can be input by the user
     * @param upperBound                  the maximum value which can be input by the user
     * @param messageToShow               the message to show to let the user know why they have to input a number
     * @param numberFormatErrMsgBeginning the first part of the error message shown if the user does not input an {@code Integer}
     * @return the number input by the user (a correct one)
     */
    private int askForInteger(int lowerBound, int upperBound, String messageToShow, String numberFormatErrMsgBeginning) throws IOException {
        int res;

        do {
            System.out.print(messageToShow);
            String string = stdIn.readLine();
            try {
                res = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                res = -1;       // remains in the do-while cycle
                System.out.println(numberFormatErrMsgBeginning + MessageResourceBundle.getMessage("must_be_number"));
            }
        } while (res < lowerBound || res > upperBound);

        return res;
    }

    /**
     * Asks the user for a {@code Color}
     *
     * @return the {@code Color} picked by the user
     */
    public Color askForColor() throws IOException {
        String res;
        Color color;
        do {
            System.out.print(MessageResourceBundle.getMessage("ask_color"));
            res = stdIn.readLine();
            try {
                color = Color.valueOf(res.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println(MessageResourceBundle.getMessage("possible_colors"));
                color = null;
            }
        } while (color == null);

        return color;
    }

    /**
     * Asks the player to choose whether to play in expert mode or not
     *
     * @return true if the player wants to play in expert mode, false otherwise
     */
    public boolean askExpertMode() throws IOException {
        String res;

        do {
            System.out.print(MessageResourceBundle.getMessage("ask_expert"));
            res = stdIn.readLine();
            res = res.toLowerCase(Locale.ROOT);
        } while (!res.equals("y") && !res.equals("n") && !res.equals(""));

        return res.equals("y") || res.equals("");
    }
}
