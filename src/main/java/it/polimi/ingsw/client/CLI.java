package it.polimi.ingsw.client;

import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_state.GameState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CLI extends Client {
    private final BufferedReader stdIn;

    public CLI() {
        stdIn = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public int getAssistantValue() throws IOException {
        return askForInteger(1, 10, "Insert value of the selected assistant [1-10]: ", "Assistant value");
    }

    @Override
    public String askUsername() throws IOException {
        System.out.print(Messages.ASK_USERNAME);
        String username = stdIn.readLine();
        setUsername(username);
        return username;
    }

    @Override
    public int askNumPlayers() throws IOException {
        return askForInteger(2, 4, "Insert desired number of players (2, 3 or 4): ", "Number of players");
    }

    @Override
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
    public Color pickColor() {
        return null;
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
    public int chooseAction(int bound) throws IOException {
        int res;

        do {
            System.out.print("Select the corresponding number: ");
            String string = stdIn.readLine();
            try {
                res = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                res = -1;       // remains in the do-while cycle
                System.out.println("Action identifier must be a number!");
            }

        } while (res < 1 || res > bound);

        return res - 1;
    }

    @Override
    public int askCharacterIndex() throws IOException {
        return askForInteger(1, 3, "Insert the number of the character you want to play [1-3]: ", "Character index");
    }

    @Override
    public void showAllCharactersWithIndex() {
        for (int i = 0; i < getCharacters().length; i++) {
            System.out.println((i + 1) + ". " + getCharacters()[i]);
        }
    }

    @Override
    public void endGame(String message) {
        showMessage(message);
        System.exit(0);
    }

    @Override
    public ArrayList<Color> askColorListForSwapCharacters(int maxBound, String secondElement) throws IOException {
        int size = askForInteger(0, maxBound, "Choose how many students you want to swap: ", "Number of students");
        System.out.println("Select " + size + " students from your entrance");
        ArrayList<Color> selectedColors = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Color toAdd = askStudentColor();
            selectedColors.add(toAdd);
        }
        System.out.println("Select " + size + " students from " + secondElement);
        for (int i = 0; i < size; i++) {
            Color toAdd = askStudentColor();
            selectedColors.add(toAdd);
        }
        return selectedColors;
    }

    @Override
    public Color askStudentColor() throws IOException {
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
    public int askIslandIndex() throws IOException {
        return askForInteger(1, 12, "Insert number of the selected island: ", "Island index");
    }

    @Override
    public int askNumStepsOfMotherNature() throws IOException {
        return askForInteger(0, 15, "Insert number of steps of mother nature: ", "Number of steps");
    }

    @Override
    public int askCloudIndex() throws IOException {
        return askForInteger(1, 4, "Insert number of the cloud you want to pick: ", "Cloud index");
    }

    @Override
    public void showPossibleActions(List<String> actions) {
        showMessage("You have the following actions: ");
        for (int i = 0; i < actions.size(); i++) {
            System.out.println((i + 1) + ". " + actions.get(i));
        }
    }


    /**
     * Shows the message to the user
     *
     * @param message the message to be shown
     */
    @Override
    public void showMessage(String message) {
        printBlueLine();
        System.out.println(message);
    }

    /**
     * Prints a line containing only "-" characters
     */
    private void printBlueLine() {
        System.out.println(CliView.BLUE_LINE);
    }

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
