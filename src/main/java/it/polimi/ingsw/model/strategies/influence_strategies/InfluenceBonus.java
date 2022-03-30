package it.polimi.ingsw.model.strategies.influence_strategies;

import it.polimi.ingsw.model.game_objects.Island;
import it.polimi.ingsw.model.Player;

public class InfluenceBonus implements InfluenceStrategy {
    private final Player buffedPlayer;
    private final int bonus;

    public InfluenceBonus(Player buffedPlayer, int bonus) {
        this.buffedPlayer = buffedPlayer;
        this.bonus = bonus;
    }

    public InfluenceBonus(Player buffedPlayer) {
        this.buffedPlayer = buffedPlayer;
        this.bonus = 2;
    }

    /**
     * The method to calculate the influence of the selected {@code Island} when the effect of the
     * {@code Character} called "plus2Influence" is active
     *
     * @param island the {@code Island} to be considered
     * @param player the {@code Player} to calculate the influence of
     * @return an int representing the influence of the player on the island
     */
    @Override
    public int computeInfluence(Island island, Player player) {
        return InfluenceDefault.computeDefaultInfluence(island, player) + (player == buffedPlayer ? bonus : 0);
    }
}
