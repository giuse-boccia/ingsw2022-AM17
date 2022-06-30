package it.polimi.ingsw.model.strategies.influence_strategies;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;

import java.util.ArrayList;

public class InfluenceBonus implements InfluenceStrategy {
    private final Player buffedPlayer;
    private final int bonus;

    public InfluenceBonus(Player buffedPlayer, int bonus) {
        this.buffedPlayer = buffedPlayer;
        this.bonus = bonus;
    }

    /**
     * The method to calculate the influence of the selected {@code Island} when the effect of the
     * {@code Character} called "plus2Influence" is active
     *
     * @param island the {@code Island} to be considered
     * @param team   the {@code Player} to calculate the influence of
     * @return an int representing the influence of the player on the island
     */
    @Override
    public int computeInfluence(Island island, ArrayList<Player> team) {
        return InfluenceDefault.computeDefaultInfluence(island, team) + (team.contains(buffedPlayer) ? bonus : 0);
    }
}
