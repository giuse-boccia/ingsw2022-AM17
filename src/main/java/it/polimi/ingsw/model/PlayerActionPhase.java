package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.CharacterAlreadyPlayedException;
import it.polimi.ingsw.exceptions.InvalidCharacterException;

import java.util.ArrayList;

public class PlayerActionPhase {
    private final Assistant assistant;
    private final GameBoard gb;
    private Character playedCharacter;
    private InfluenceStrategy influenceStrategy;
    private ProfessorStrategy professorStrategy;
    private MNStrategy mnStrategy;


    public PlayerActionPhase(Assistant assistant, GameBoard gb) {
        this.assistant = assistant;
        this.gb = gb;
        this.playedCharacter = null;
        this.influenceStrategy = new InfluenceDefault();
        this.professorStrategy = new ProfessorDefault();
        this.mnStrategy = new MNDefault();
    }

    /**
     * Resolves the island where mother nature is and possibly changes that island's owner.
     * In case of tie between two or more players, the owner of the island is not changed
     */
    private void resolveIsland() {
        Island islandToResolve = gb.getIslands().get(gb.getMotherNatureIndex());
        ArrayList<Player> players = gb.getGame().getPlayers();

        Player newOwner = null;
        int maxInfluence = 0;

        for (Player player : players) {
            int influence = influenceStrategy.computeInfluence(islandToResolve, player);

            if (influence == maxInfluence) {
                newOwner = null;
            } else if (influence > maxInfluence) {
                maxInfluence = influence;
                newOwner = player;
            }
        }

        if (newOwner != null) {
            islandToResolve.setOwner(newOwner);
        }
    }

    /**
     * Checks if the player who owns myDiningRoom can steal the professor from the player who owns
     * otherDiningRoom
     *
     * @param color           the {@code Color} to check
     * @param myDiningRoom    the {@code DiningRoom} of the {@code Player} who wants to steal the {@code Professor}
     * @param otherDiningRoom the {@code DiningRoom} of the {@code Player} who owns the {@code Professor}
     * @return true if the owner of myDiningRoom can steal the professor
     */
    public boolean canStealProfessor(Color color, DiningRoom myDiningRoom, DiningRoom otherDiningRoom) {
        return professorStrategy.canStealProfessor(color, myDiningRoom, otherDiningRoom);
    }

    /**
     * Returns the maximum number of steps that mother nature can do this turn
     *
     * @param card the {@code Assistant} card played this turn
     * @return the maximum number of steps that mother nature can do this turn
     */
    public int getMNMaxSteps(Assistant card) {
        return mnStrategy.getMNMaxSteps(card);
    }

    public void playPassiveCharacter(PassiveCharacter playedCharacter) throws CharacterAlreadyPlayedException, InvalidCharacterException {
        if (this.playedCharacter != null)
            throw new CharacterAlreadyPlayedException("You already played a character this turn");
        this.playedCharacter = playedCharacter;

        switch (playedCharacter.getCardName()) {
            case plus2MNMoves:
                this.mnStrategy = new MNBonus();
                break;
            case takeProfWithEqualStudents:
                this.professorStrategy = new ProfessorWithDraw();
                break;
            case plus2Influence:
                this.influenceStrategy = new InfluenceBonus(assistant.getPlayer());
                break;
            case ignoreTowers:
                this.influenceStrategy = new InfluenceIgnoreTowers();
                break;
            case ignoreColor:
                Color ignoredColor = ((PassiveCharacterWithColor) playedCharacter).getColor();
                this.influenceStrategy = new InfluenceIgnoreColor(ignoredColor);
                break;
            default:
                throw new InvalidCharacterException("Not a passive character");
        }
    }

}
