package it.polimi.ingsw.model;

import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Dashboard;

import java.util.ArrayList;

public class Player {
    private final ArrayList<Assistant> hand;
    private final Dashboard dashboard;
    private final String name;
    private int numCoins;
    private Wizard wizard = null;
    private final int initialTowers;

    public Player(String name, int initialTowers) {
        this.name = name;
        this.initialTowers = initialTowers;
        this.numCoins = 1;
        this.dashboard = new Dashboard();
        this.hand = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            hand.add(new Assistant(i % 2 != 0 ? i / 2 + 1 : i / 2, i, this));
        }
    }

    public void moveStudent(Place from, Place to, Student student) {
        from.giveStudent(to, student);
        // TODO: If move TO my diningRoom check if can steal professor using playerActionPhase Strategy
    }

    public void playAssistant(Assistant assistant) {

    }

    public void useCharacter(Character character) {
        // chiama charachter.useEffect() e toglie monete e lancia eccezione se non ha abbastanza monete
    }

    public void pickWizard(Wizard wizard) {
        // TODO: check if wizard is not picked by others
        this.wizard = wizard;
    }

    private void checkProfessors(Color color) {
        // Steal professor if possible
    }

    public void fillFromCloud(Cloud cloud) {

    }

    /**
     * Returns the remaining towers in the player dashboard
     *
     * @return the remaining towers in the player dashboard
     */
    public int getNumberOfTowers(Game game) {
        int res = initialTowers;
        for (Island island : game.getGameBoard().getIslands()) {
            if (island.getOwner() == this) {
                res -= island.getNumOfTowers();
            }
        }
        return res;
    }

    public ArrayList<Assistant> getHand() {
        return new ArrayList<>(hand);
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

    public Wizard getWizard() {
        return wizard;
    }
}
