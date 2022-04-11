package it.polimi.ingsw.model.game_actions;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.dashboard_objects.DiningRoom;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Entrance;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Cloud;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.model.strategies.influence_strategies.*;
import it.polimi.ingsw.model.strategies.mn_strategies.MNBonus;
import it.polimi.ingsw.model.strategies.mn_strategies.MNDefault;
import it.polimi.ingsw.model.strategies.mn_strategies.MNStrategy;
import it.polimi.ingsw.model.strategies.professor_strategies.ProfessorDefault;
import it.polimi.ingsw.model.strategies.professor_strategies.ProfessorStrategy;
import it.polimi.ingsw.model.strategies.professor_strategies.ProfessorWithDraw;
import it.polimi.ingsw.model.utils.Students;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerActionPhase {
    protected final Assistant assistant;
    protected final GameBoard gb;
    protected Character playedCharacter;
    protected InfluenceStrategy influenceStrategy;
    protected ProfessorStrategy professorStrategy;
    protected MNStrategy mnStrategy;
    int numStudentsMoved = 0;
    boolean mnMoved = false;


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
                    islandToResolve.decreaseNoEntryNum();
                    return;
                }
            }
        }

        Map<TowerColor, ArrayList<Player>> teamMap = getTeams(gb.getGame().getPlayers());

        TowerColor newOwner = null;
        int maxInfluence = 0;

        for (TowerColor team : teamMap.keySet()) {
            int influence = influenceStrategy.computeInfluence(islandToResolve, teamMap.get(team));

            if (influence == maxInfluence) {
                newOwner = null;
            } else if (influence > maxInfluence) {
                maxInfluence = influence;
                newOwner = team;
            }
        }

        if (newOwner != null) {
            gb.setIslandOwner(islandToResolve, newOwner);
        }
    }

    /**
     * Given an ArrayList of players, return a map from a tower color to the players who have that tower color
     *
     * @param players the players to map into tower colors
     * @return a map from a tower color to the players who have that tower color
     */
    private Map<TowerColor, ArrayList<Player>> getTeams(ArrayList<Player> players) {
        Map<TowerColor, ArrayList<Player>> res = new HashMap<>();

        for (TowerColor tc : TowerColor.values()) {
            ArrayList<Player> team = new ArrayList<>(players.stream()
                    .filter((player -> player.getTowerColor() == tc))
                    .toList());

            res.put(tc, team);
        }

        return res;
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
     * Checks if the current player can steal the professor of the given color and eventually sets him/her as
     * its owner
     *
     * @param color the {@code Colo} of the professor to steal
     */
    private void stealProfessorIfPossible(Color color) {

        Player owner = gb.getOwnerOfProfessor(color);   // null if nobody owns the professor (see method javadoc)

        if (owner == null) {
            gb.setOwnerOfProfessor(color, getCurrentPlayer());
            return;
        }

        if (professorStrategy.canStealProfessor(
                color,
                getCurrentPlayer().getDashboard().getDiningRoom(),
                owner.getDashboard().getDiningRoom())) {
            gb.setOwnerOfProfessor(color, getCurrentPlayer());
        }
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
            throws InvalidCharacterException, CharacterAlreadyPlayedException, StudentNotOnTheCardException, InvalidActionException, InvalidStudentException, NotEnoughCoinsException {
        if (!gb.getGame().isExpert()) {
            throw new InvalidActionException("There are no characters in the non expert mode");
        }
        if (getCurrentPlayer().getNumCoins() < character.getCost()) {
            throw new NotEnoughCoinsException("You don't have enough coins to play this character");
        }
        if (this.playedCharacter != null) {
            throw new CharacterAlreadyPlayedException("You already played a character this turn");
        }
        this.playedCharacter = character;
        character.useEffect(this, island, color, srcStudents, dstStudents);
        getCurrentPlayer().removeCoins(character.getCost());
        character.addCoinAfterFirstUse();
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

    /**
     * Moves a {@code Student} of the selected {@code Color} to the selected {@code Place}
     *
     * @param color       the {@code Colo} of the {@code Student} to move
     * @param destination the destination to move the {@code Student} to
     * @throws InvalidActionException  if the action is not valid
     * @throws InvalidStudentException if the {@code Player} does not own a {@code Student} of the selected {@code Color}
     */
    public void moveStudent(Color color, Place destination) throws InvalidActionException, InvalidStudentException {
        // TODO InvalidStudent - if I don't own a student of this color, InvalidPlace if I attempt to move it into
        // TODO the Entrance (it happens when entrance.getStudents() == 7-numStudPlayed

        if (numStudentsMoved == gb.getGame().getPlayers().size() + 1) {
            throw new InvalidActionException("You have already moved " + numStudentsMoved + " students");
        }

        Entrance entrance = getCurrentPlayer().getDashboard().getEntrance();

        entrance.giveStudent(
                destination, Students.findFirstStudentOfColor(entrance.getStudents(), color)
        );

        numStudentsMoved++;

        stealProfessorIfPossible(color);

        if (destination == getCurrentPlayer().getDashboard().getDiningRoom()) {
            if (getCurrentPlayer().getDashboard().getDiningRoom().getNumberOfStudentsOfColor(color) % 3 == 0) {
                getCurrentPlayer().addCoin();
            }
        }
    }

    /**
     * Moves MotherNature of the selected number of steps (numSteps)
     *
     * @param numSteps the number of steps which MotherNature should move of
     * @throws InvalidActionException               if the action is not valid
     * @throws InvalidStepsForMotherNatureException if the number of steps selected is greater than the allowed number of steps
     */
    // FIXME Player check could be done in Game (if it's the Facade), which calls PlayerActionPhase - just a supposition though
    public void moveMotherNature(int numSteps) throws InvalidActionException, InvalidStepsForMotherNatureException {

        checkInvalidAction();

        if (numSteps < 0 || numSteps > mnStrategy.getMNMaxSteps(assistant)) {
            throw new InvalidStepsForMotherNatureException("Invalid move for mother nature");
        }

        gb.moveMotherNature(numSteps);
        resolveIsland();

        mnMoved = true;
    }

    /**
     * Chooses a cloud not yet chosen ad fills the selected {@code Player} {@code Entrance} with the students on it
     *
     * @param cloudIndex the index of the {@code Cloud}
     * @throws InvalidActionException if the action is not valid
     * @throws InvalidCloudException  if the {@code Cloud} is not valid
     */
    public void chooseCloud(int cloudIndex) throws InvalidActionException, InvalidCloudException {

        checkInvalidAction();

        if (!mnMoved) {
            throw new InvalidActionException("Move mother nature first");
        }

        if (cloudIndex < 0 || cloudIndex >= gb.getClouds().size() || gb.getClouds().get(cloudIndex).isEmpty()) {
            throw new InvalidCloudException("The selected cloud is not valid");
        }

        Cloud cloud = gb.getClouds().get(cloudIndex);
        cloud.emptyTo(getCurrentPlayer().getDashboard().getEntrance());

        // The PlayerActionPhase is finished
        gb.getGame().getCurrentRound().nextPlayerActionPhase();
    }

    /**
     * Checks if the action selected from the selected {@code Player} is not valid
     *
     * @throws InvalidActionException if the action is not valid
     */
    private void checkInvalidAction() throws InvalidActionException {

        int studentsToMove = gb.getGame().getPlayers().size() % 2 == 0 ? 3 : 4;
        if (numStudentsMoved < studentsToMove) {
            throw new InvalidActionException("Move your students first");
        }
    }

    public Player getCurrentPlayer() {
        return assistant.getPlayer();
    }

}
