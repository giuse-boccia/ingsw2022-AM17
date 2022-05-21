package it.polimi.ingsw.client;

import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_state.GameState;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CLI extends Client {
    private final BufferedReader stdIn;

    public CLI() {
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        // TODO: move the following comment in Project WIKI
        // NOTE: for correct visualization in Windows Terminal, run command "chcp 65001" before "java -jar ..."
    }

    @Override
    public void getAssistantValue() throws IOException {
        int value = askForInteger(1, 10, "Insert value of the selected assistant [1-10]: ", "Assistant value");
        getCurrentObserver().sendActionParameters("PLAY_ASSISTANT", null, null, null, null,
                value, null, null, null);
    }

    @Override
    public void askUsername() throws IOException {
        System.out.print(Messages.ASK_USERNAME);
        String username = stdIn.readLine();
        setTmpUsername(username);
        getCurrentObserver().sendLoginParameters(username, null, null);
    }

    @Override
    public void askNumPlayersAndExpertMode() throws IOException {
        int numPlayers = askForInteger(2, 4, "Insert desired number of players (2, 3 or 4): ", "Number of players");
        boolean isGameExpert = askExpertMode();
        getCurrentObserver().sendLoginParameters(null, numPlayers, isGameExpert);
    }

    /**
     * Asks the player to choose whether to play in expert mode or not
     *
     * @return true if the player wants to play in expert mode, false otherwise
     */
    public boolean askExpertMode() throws IOException {
        String res;

        do {
            System.out.print("Do you want to play in expert mode? [Y/n] ");
            res = stdIn.readLine();
            res = res.toLowerCase(Locale.ROOT);
        } while (!res.equals("y") && !res.equals("n") && !res.equals(""));

        return res.equals("y") || res.equals("");
    }

    @Override
    public void showCurrentLobby(GameLobby lobby) {
        printBlueLine();
        String message = "GAME: " + lobby.getPlayers().length;
        if (lobby.getNumPlayers() != -1) {
            message += "/" + lobby.getNumPlayers();
        }
        message += " players | ";
        if (lobby.getNumPlayers() != -1) {
            message += "Expert mode: " + (lobby.isExpert() ? "Active" : "Not active");
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
        System.out.println(Messages.GRACEFUL_TERM);
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
            System.out.print("Select the corresponding number: ");
            String string = stdIn.readLine();
            try {
                res = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                res = -1;       // remains in the do-while cycle
                System.out.println("Action identifier must be a number!");
            }

        } while (res < 1 || res > size);

        getCurrentObserver().sendActionName(actions.get(res - 1));
    }

    @Override
    public void askIslandIndexForCharacter(CharacterName characterName) throws IOException {
        int islandIndex = askForInteger(1, 12, "Insert number of the selected island: ", "Island index");
        getCurrentObserver().sendActionParameters("PLAY_CHARACTER", null, islandIndex - 1, null,
                null, null, characterName, null, null);
    }

    @Override
    public void pickColorForPassive(CharacterName characterName) throws IOException {
        Color color = askForColor();
        getCurrentObserver().sendActionParameters("PLAY_CHARACTER", color, null, null,
                null, null, characterName, null, null);
    }

    @Override
    public void askToMoveOneStudentFromCard(boolean toIsland) throws IOException {
        Color color = askForColor();
        Integer islandIndex = null;
        CharacterName characterName = CharacterName.move1FromCardToDining;
        if (toIsland) {
            islandIndex = askForInteger(1, 12, "Insert number of the selected island: ", "Island index")
                    - 1;
            characterName = CharacterName.move1FromCardToIsland;
        }
        getCurrentObserver().sendActionParameters("PLAY_CHARACTER", null, islandIndex, null,
                null, null, characterName, List.of(color), null);
    }

    @Override
    public void askMoveStudentToIsland() throws IOException {
        int island = askForInteger(1, 12, "Insert number of the selected island: ", "Island index");
        Color color = askForColor();
        getCurrentObserver().sendActionParameters("MOVE_STUDENT_TO_ISLAND", color, island - 1, null, null,
                null, null, null, null);
    }

    @Override
    public void askCharacterIndex() throws IOException {
        int index = askForInteger(1, 3, "Insert the number of the character you want to play [1-3]: ", "Character index");
        getCurrentObserver().sendCharacterName(getCharacters().get(index - 1).getCharacterName());
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
    public void playCharacterWithoutArguments(CharacterName characterName) throws IOException {
        getCurrentObserver().sendActionParameters("PLAY_CHARACTER", null, null, null, null,
                null, characterName, null, null);
    }

    @Override
    public void askColorListForSwapCharacters(int maxBound, String secondElement, CharacterName characterName) throws IOException {
        int size = askForInteger(0, maxBound, "Choose how many students you want to swap: ", "Number of students");
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
        getCurrentObserver().sendActionParameters("PLAY_CHARACTER", null, null, null, null,
                null, characterName, srcColors, dstColors);
    }

    @Override
    public void askMoveStudentToDining() throws IOException {
        Color color = askForColor();
        getCurrentObserver().sendActionParameters("MOVE_STUDENT_TO_DINING", color, null, null, null,
                null, null, null, null);
    }

    public Color askForColor() throws IOException {
        String res;
        Color color;
        do {
            System.out.print("Write the color of the student: ");
            res = stdIn.readLine();
            try {
                color = Color.valueOf(res.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Color not recognised [blue | green | yellow | red | pink]");
                color = null;
            }
        } while (color == null);

        return color;
    }

    @Override
    public void askNumStepsOfMotherNature() throws IOException {
        int steps = askForInteger(0, 15, "Insert number of steps of mother nature: ", "Number of steps");
        getCurrentObserver().sendActionParameters("MOVE_MN", null, null, steps, null,
                null, null, null, null);
    }

    @Override
    public void askCloudIndex() throws IOException {
        int cloud = askForInteger(1, 4, "Insert number of the cloud you want to pick: ", "Cloud index");
        getCurrentObserver().sendActionParameters("FILL_FROM_CLOUD", null, null, null, cloud - 1,
                null, null, null, null);
    }

    @Override
    public void showPossibleActions(List<String> actions) {
        showMessage("You have the following actions: ");
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
                System.out.println(numberFormatErrMsgBeginning + " must be a number!");
            }
        } while (res < lowerBound || res > upperBound);

        return res;
    }

    @Override
    public void updateGameState(GameState gameState) {
        printBlueLine();
        CliView.printGameState(gameState, getUsername());
    }
}
