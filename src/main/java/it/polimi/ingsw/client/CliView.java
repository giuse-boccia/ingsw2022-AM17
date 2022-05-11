package it.polimi.ingsw.client;

import it.polimi.ingsw.constants.ConsoleColors;
import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.model.game_state.CharacterState;
import it.polimi.ingsw.model.game_state.GameState;
import it.polimi.ingsw.model.game_state.IslandState;
import it.polimi.ingsw.model.utils.Students;

/**
 * This class contains methods to print and visualize the current state of the game in the CLI
 */
public class CliView {
    public static final String BLUE_LINE = ConsoleColors.BLUE +
            "------------------------------------------------------------"
            + ConsoleColors.RESET;

    public static final String ISLANDS_LINE = ConsoleColors.GREEN +
            "------------------------ISLANDS-----------------------------"
            + ConsoleColors.RESET;

    public static final String CHARACTERS_LINE = ConsoleColors.GREEN +
            "-----------------------CHARACTERS---------------------------"
            + ConsoleColors.RESET;

    public static final String MN_INDICATOR = "<-------- MN is here";

    /**
     * Prints a GameState to {@code System.out}. Uses ANSI colors.
     * A 50x80 window is recommended
     *
     * @param gameState the {@code GameState] to print
     */
    public static void printGameState(GameState gameState, String currentPlayer) {
        // Islands
        System.out.println(ISLANDS_LINE);
        for (int i = 0; i < gameState.getIslands().size(); i++) {
            printIslandState(gameState.getIslands().get(i), i, gameState.getMNIndex(), gameState.isExpert());
        }

        // Characters
        if (gameState.isExpert()) {
            System.out.println(CHARACTERS_LINE);
            for (int i = 0; i < gameState.getCharacters().size(); i++) {
                printCharacterState(gameState.getCharacters().get(i), i);
            }
        }

        // TODO: current player dashboard

        // TODO: others player dashboard
    }

    /**
     * Prints a single line visualizing the state of an island, including its students, owner, noEntry tiles and if
     * Mother Nature is currently there
     * ES: Island 1 | BLACK | ●●●●● | XX | <----- MN is here
     *
     * @param islandState an Island State, included in a Game State
     * @param islandIndex the current index of the island (starting from 0)
     * @param MNIndex     the current index of mother nature
     * @param isExpert    whether the game is in expert mode (if it is then noEntries are displayed)
     */
    private static void printIslandState(IslandState islandState, int islandIndex, int MNIndex, boolean isExpert) {
        // Island index
        System.out.print("Island " + (islandIndex + 1) + " | ");

        // Island owner
        String ownerString = "FREE";
        if (islandState.getTowerColor() != null) {
            ownerString = islandState.getTowerColor().name();
        }
        System.out.print(ownerString + " | ");

        // Students
        if (islandState.getStudents().size() != 0) {
            System.out.print(Students.getStringFromList(islandState.getStudents()));
        } else {
            System.out.print(ConsoleColors.WHITE + "empty" + ConsoleColors.RESET);
        }
        System.out.print(" | ");

        // NoEntry
        if (isExpert && islandState.getNoEntryNum() != 0) {
            System.out.print('X' * islandState.getNoEntryNum() + " | ");
        }

        // MN indicator
        if (islandIndex == MNIndex) {
            System.out.print("\t\t" + MN_INDICATOR);
        }

        // print new line character
        System.out.println();
    }

    /**
     * Prints a single line visualizing the state of a character, including his name and cost
     *
     * @param characterState a character state, included in a {@code GameState}}
     * @param characterIndex the index of this character
     */
    private static void printCharacterState(CharacterState characterState, int characterIndex) {
        // Character index
        System.out.print(characterIndex + 1 + ". ");

        // Character name (es: move1FromCardToIsland)
        System.out.print(characterState.getCharacterName() + " ");

        // Cost
        System.out.print("(" + characterState.getCost() + " " + (characterState.getCost() == 1 ? "coin" : "coins") + ")");

        // Students (if any)
        if (characterState.getStudents() != null && !characterState.getStudents().isEmpty()) {
            System.out.print(" | " + Students.getStringFromList(characterState.getStudents()));
        }

        System.out.println();
    }
}

