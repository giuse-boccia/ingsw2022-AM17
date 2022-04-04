package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Dashboard;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Cloud;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;

import java.util.List;

public class Player {
    private final Assistant[] hand;
    private final Dashboard dashboard;
    private final String name;
    private int numCoins;
    private Wizard wizard = null;
    private Game game;
    private final int initialTowers;

    public Player(String name, int initialTowers) {
        this.name = name;
        this.initialTowers = initialTowers;
        this.numCoins = 1;
        this.dashboard = new Dashboard();
        this.hand = new Assistant[10];
        for (int i = 1; i <= 10; i++) {
            hand[i - 1] = new Assistant(i % 2 != 0 ? i / 2 + 1 : i / 2, i, this);
        }
    }

    /**
     * Plays an {@code Assistant} from the hand of the {@code Player}
     *
     * @param assistant the {@code Assistant} to play
     */
    public void playAssistant(Assistant assistant) {
        hand[assistant.getValue() - 1] = null;
    }

    /**
     * Picks a {@code Wizard} not been picked by another {@code Player} yet
     *
     * @param wizard the {@code Wizard} chosen
     */
    public void pickWizard(Wizard wizard) {
        // TODO: check if wizard is not picked by others
        this.wizard = wizard;
    }

    /**
     * Add the {@code Player} to the given {@code Game}
     *
     * @param game the {@code Game} to add the {@code Player} to
     */
    public void addToGame(Game game) {
        this.game = game;
    }

    /**
     * Returns the remaining towers in the player dashboard
     *
     * @return the remaining towers in the player dashboard
     */
    public int getNumberOfTowers() {
        int res = initialTowers;
        for (Island island : game.getGameBoard().getIslands()) {
            if (island.getOwner() == this) {
                res -= island.getNumOfTowers();
            }
        }
        return res;
    }

    /**
     * Returns a {@code List} of the colors of the professors owned by the player
     *
     * @return a {@code List} of the colors of the professors owned by the player
     */
    public List<Color> getColorsOfOwnedProfessors() {
        return game.getGameBoard()
                .getColorsOfOwnedProfessors(this);
    }

    /**
     * Checks if the {@code Player} controls the professor of the given {@code Color}
     *
     * @param color the color of the professor to check
     * @return true if the {@code Player} controls the professor of the given {@code Color}
     */
    public boolean hasProfessor(Color color) {
        return game.getGameBoard()
                .getColorsOfOwnedProfessors(this)
                .contains(color);
    }

    public Assistant[] getHand() {
        return hand.clone();
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public String getName() {
        return name;
    }

    public int getNumCoins() {
        return numCoins;
    }

    public void addCoin() {
        numCoins++;
    }

    public Wizard getWizard() {
        return wizard;
    }
}
