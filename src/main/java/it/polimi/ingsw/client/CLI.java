package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.game_objects.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class CLI extends Client {
    private final BufferedReader stdIn;

    public CLI() {
        stdIn = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public String askUsername() throws IOException {
        System.out.print("Insert username: ");
        return stdIn.readLine();
    }

    @Override
    public int askNumPlayers() throws IOException {
        int res;

        do {
            System.out.print("Insert desired number of players (2, 3 or 4): ");
            String string = stdIn.readLine();
            try {
                res = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                res = -1;       // remains in the do-while cycle
                System.out.println("Number of players must be a number!");
            }
        } while (res < 2 || res > 4);

        return res;
    }

    @Override
    public boolean askExpertMode() throws IOException {
        String res = null;

        do {
            System.out.print("Do you want to play in expert mode? [Y/n] ");
            res = stdIn.readLine();
            res = res.toLowerCase(Locale.ROOT);
        } while (!res.equals("y") && !res.equals("n") && !res.equals(""));

        return res.equals("y") || res.equals("");
    }

    @Override
    public void showCurrentLobby(GameLobby lobby) {
        clearCommandWindow();
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
        System.out.println("");
    }

    @Override
    public Color pickColor() {
        return null;
    }

    @Override
    public void gracefulTermination(String message) {
        clearCommandWindow();
        System.out.println(message);
        System.out.println("Application will now close...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(-1);
    }

    /**
     * Shows the message to the user
     *
     * @param message the message to be shown
     */
    @Override
    public void showMessage(String message) {
        clearCommandWindow();
        System.out.println(message);
    }

    /**
     * Prints a line containing only "-" characters
     */
    private void clearCommandWindow() {
        System.out.println("------------------------------------------------------------");
    }
}
