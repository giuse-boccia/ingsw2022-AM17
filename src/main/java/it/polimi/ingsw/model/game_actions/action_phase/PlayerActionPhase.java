package it.polimi.ingsw.model.game_actions.action_phase;

import it.polimi.ingsw.exceptions.CharacterAlreadyPlayedException;
import it.polimi.ingsw.exceptions.InvalidCharacterException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.dashboard_objects.DiningRoom;
import it.polimi.ingsw.model.strategies.influence_strategies.*;
import it.polimi.ingsw.model.strategies.mn_strategies.MNBonus;
import it.polimi.ingsw.model.strategies.mn_strategies.MNDefault;
import it.polimi.ingsw.model.strategies.mn_strategies.MNStrategy;
import it.polimi.ingsw.model.strategies.professor_strategies.ProfessorDefault;
import it.polimi.ingsw.model.strategies.professor_strategies.ProfessorStrategy;
import it.polimi.ingsw.model.strategies.professor_strategies.ProfessorWithDraw;

import java.util.ArrayList;

public abstract class PlayerActionPhase {
    protected final Assistant assistant;
    protected final GameBoard gb;
    protected Character playedCharacter;
    protected InfluenceStrategy influenceStrategy;
    protected ProfessorStrategy professorStrategy;
    protected MNStrategy mnStrategy;


    public PlayerActionPhase(Assistant assistant, GameBoard gb) {
        this.assistant = assistant;
        this.gb = gb;
        this.playedCharacter = null;
        this.influenceStrategy = new InfluenceDefault();
        this.professorStrategy = new ProfessorDefault();
        this.mnStrategy = new MNDefault();
    }

    /**
     * Resolves an island and possibly changes that island's owner.
     * In case of tie between two or more players, the owner of the island is not changed
     *
     * @param islandToResolve the island to resolve
     */
    public void resolveIsland(Island islandToResolve) {

        if (islandToResolve.getNoEntryNum() > 0) {
            for (Character character : gb.getGame().getGameBoard().getCharacters()) {
                if (character.getCardName() == CharacterName.noEntry) {
                    NoEntryCharacter noEntryCharacter = (NoEntryCharacter) character;
                    noEntryCharacter.addNoEntry();
                    return;
                }
            }
        }

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
            gb.setIslandOwner(islandToResolve, newOwner);
        }
    }

    /**
     * Resolves the island where mother nature is and eventually changes that island's owner.
     * In case of tie between two or more players, the owner of the island is not changed
     */
    public void resolveIsland() {
        resolveIsland(gb.getIslands().get(gb.getMotherNatureIndex()));
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
     * @return the maximum number of steps that mother nature can do this turn
     */
    public int getMNMaxSteps() {
        return mnStrategy.getMNMaxSteps(this.assistant);
    }

    /**
     * Method to use the effect of a character and to check if one had already been used this turn
     *
     * @param character The character which we want to use the effect of
     * @throws InvalidCharacterException       if the {@code Character} is not valid
     * @throws CharacterAlreadyPlayedException if a {@code Character} has already been used
     */
    public void playCharacter(Character character, Island island, Color color, ArrayList<Student> srcStudents, ArrayList<Student> dstStudents)
            throws InvalidCharacterException, CharacterAlreadyPlayedException {
        if (this.playedCharacter != null)
            throw new CharacterAlreadyPlayedException("You already played a character this turn");
        this.playedCharacter = character;
        character.useEffect(this, island, color, srcStudents, dstStudents);
    }

    /**
     * Instances the correct strategy accordingly to the playedCharacter
     *
     * @param playedCharacter the {@code Character} which we want to set the correct strategy accordingly to
     */
    public void playPassiveCharacter(PassiveCharacter playedCharacter) {
        switch (playedCharacter.getCardName()) {
            case plus2MNMoves -> this.mnStrategy = new MNBonus();
            case takeProfWithEqualStudents -> this.professorStrategy = new ProfessorWithDraw();
            case plus2Influence -> this.influenceStrategy = new InfluenceBonus(assistant.getPlayer());
            case ignoreTowers -> this.influenceStrategy = new InfluenceIgnoreTowers();
        }
    }

    /**
     * Instances the {@code InfluenceIgnoreColor} strategy
     *
     * @param color the color which we want to ignore in the influence compute
     */
    public void playPassiveCharacterWithColor(Color color) {
        this.influenceStrategy = new InfluenceIgnoreColor(color);
    }

    public Player getCurrentPlayer() {
        return assistant.getPlayer();
    }

    public abstract void play();

}
