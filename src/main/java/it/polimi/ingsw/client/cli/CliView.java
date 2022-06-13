package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.constants.ConsoleColors;
import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.server.game_state.*;
import it.polimi.ingsw.model.utils.Students;

import java.util.List;

/**
 * This class contains methods to print and visualize the current state of the game in the CLI
 */
public class CliView {
    public static final String BLUE_LINE = ConsoleColors.BLUE +
            "------------------------------------------------------------------------------------------------------------------------"
            + ConsoleColors.RESET;

    public static final String ISLANDS_LINE = ConsoleColors.GREEN +
            "----------------------------------------------------ISLANDS-------------------------------------------------------------"
            + ConsoleColors.RESET;

    public static final String CHARACTERS_LINE = ConsoleColors.GREEN +
            "---------------------------------------------------CHARACTERS-----------------------------------------------------------"
            + ConsoleColors.RESET;

    public static final String ASSISTANTS_LINE = ConsoleColors.GREEN +
            "------------------------------------------------YOUR ASSISTANTS---------------------------------------------------------"
            + ConsoleColors.RESET;

    public static final String DASHBOARD_LINE = ConsoleColors.GREEN +
            "---------------------------------------------------DASHBOARDS-----------------------------------------------------------"
            + ConsoleColors.RESET;

    public static final String CLOUDS_LINE = ConsoleColors.GREEN +
            "-----------------------------------------------------CLOUDS-------------------------------------------------------------"
            + ConsoleColors.RESET;

    public static final String DASHBOARD_LINE_50 =
            "|----------|-----------------------------|-------|"
                    + ConsoleColors.RESET;

    public static final String DASHBOARD_LINE_WITH_COINS =
            "|----------|-------+-----+-----+---------|-------|"
                    + ConsoleColors.RESET;

    public static final String DASHBOARD_HEADER =
            "| ENTRANCE |         DINING ROOM         | PROFS |"
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

        // Assistants
        System.out.println(ASSISTANTS_LINE);
        int[] playerAssistants = gameState.getPlayers().stream()
                .filter(playerState -> playerState.getName().equals(currentPlayer))
                .findFirst()
                .orElseThrow()
                .getAssistants();
        if (playerAssistants.length > 0) {
            for (int assistant : playerAssistants) {
                System.out.print(assistant + "   ");
            }
            System.out.println();
        } else {
            System.out.println(ConsoleColors.WHITE + Messages.NO_ASSISTANT_LEFT + ConsoleColors.RESET);
        }

        // Dashboards
        System.out.println(DASHBOARD_LINE);
        printDashboardsState(gameState.getPlayers(), gameState.isExpert());

        // Clouds
        System.out.println(CLOUDS_LINE);
        for (int i = 0; i < gameState.getClouds().size(); i++) {
            printCloudState(gameState.getClouds().get(i), i);
        }

        // addEmptyLines(gameState);   // ensures at least 50 lines have been printed
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
        System.out.print("Island " + (islandIndex + 1));
        System.out.print((islandIndex + 1 < 10) ? "  | " : " | ");

        // Island owner
        String ownerString = "FREE";
        if (islandState.getTowerColor() != null) {
            ownerString = islandState.getTowerColor().name() + " (" + islandState.getNumOfTowers() + ")";
        }
        System.out.print(ownerString + " | ");

        // Students
        if (islandState.getStudents().size() != 0) {
            System.out.print(getStringFromStudentList(islandState.getStudents()));
        } else {
            System.out.print(ConsoleColors.WHITE + "empty" + ConsoleColors.RESET);
        }

        // NoEntry
        if (isExpert && islandState.getNoEntryNum() != 0) {
            System.out.print(" | " + 'X' * islandState.getNoEntryNum() + " | ");
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
        System.out.print((characterIndex + 1) + ". ");

        // Character name (es: move1FromCardToIsland)
        System.out.print(characterState.getCharacterName() + " ");

        // Cost
        System.out.print("(" + characterState.getCost() + " " + (characterState.getCost() == 1 ? "coin" : "coins") + ")");

        // Students (if any)
        if (characterState.getStudents() != null && !characterState.getStudents().isEmpty()) {
            System.out.print(" | " + getStringFromStudentList(characterState.getStudents()));
        }

        System.out.println();
    }

    /**
     * Prints the dashboards. If the game has 3 or 4 players some dashboards might be printed side-by-side
     *
     * @param players  a list of player states, included in the game state
     * @param isExpert whether the game is in expert mode or not
     */
    private static void printDashboardsState(List<PlayerState> players, boolean isExpert) {
        switch (players.size()) {
            case 2 -> {
                printSingleDashboard(players.get(0), isExpert);
                System.out.println();
                printSingleDashboard(players.get(1), isExpert);
            }
            case 3 -> {
                printSingleDashboard(players.get(0), isExpert);
                System.out.println();
                printDoubleDashboard(players.get(1), players.get(2), isExpert);
            }
            case 4 -> {
                printDoubleDashboard(players.get(0), players.get(2), isExpert);
                System.out.println();
                printDoubleDashboard(players.get(1), players.get(3), isExpert);
            }
        }
    }

    /**
     * Prints a single line visualizing the state of a cloud (its students or "empty" if it's empty)
     * ES: Cloud 1 | ● ● ●
     *
     * @param cloudState a cloud state, included in a Game State
     * @param cloudIndex the index of the cloud (starting from 0)
     */
    private static void printCloudState(CloudState cloudState, int cloudIndex) {
        // Cloud index
        System.out.print("Cloud " + (cloudIndex + 1) + " | ");

        // Students
        if (cloudState.getStudents().size() != 0) {
            System.out.print(getStringFromStudentList(cloudState.getStudents()));
        } else {
            System.out.print(ConsoleColors.WHITE + "empty" + ConsoleColors.RESET);
        }

        // print new line character
        System.out.println();
    }

    /**
     * Prints a single dashboard
     *
     * @param player the owner of the dashboard to be printed
     */
    private static void printSingleDashboard(PlayerState player, boolean isExpert) {
        List<Student> dining = player.getDining();
        List<Student> entrance = player.getEntrance();

        // Player name and stats
        System.out.print("  " + player.getName());
        System.out.print(" | " + player.getRemainingTowers() + " towers left (" + player.getTowerColor() + ")");
        if (isExpert) {
            System.out.print(" | " +
                    player.getNumCoins() +
                    (player.getNumCoins() == 1 ? " coin" : " coins")
            );
        }
        System.out.println();

        // Header
        System.out.println(DASHBOARD_LINE_50);
        System.out.println(DASHBOARD_HEADER);
        if (isExpert) {
            System.out.println(DASHBOARD_LINE_WITH_COINS);      // coins indicator for expert mode
        } else {
            System.out.println(DASHBOARD_LINE_50);
        }

        // First line (1st entrance student + green dining)
        // something like:      |    ●  ●   |    ● ● ●                |   ▲   |
        System.out.println("|      " +
                entranceHelper(entrance, 0) + "   |   " +
                (getStringFromStudent(new Student(Color.GREEN)) + " ").repeat(Students.countColor(dining, Color.GREEN)) +
                "  ".repeat(10 - Students.countColor(dining, Color.GREEN)) + "      |   " +
                (player.getOwnedProfessors().contains(Color.GREEN) ? (ConsoleColors.GREEN + Messages.PROF_CHAR + ConsoleColors.RESET) : " ") + "   |");

        // Second line (2nd and 3rd entrance students + red dining
        System.out.println("|   " +
                entranceHelper(entrance, 1) + "  " +
                entranceHelper(entrance, 2) + "   |   " +
                (getStringFromStudent(new Student(Color.RED)) + " ").repeat(Students.countColor(dining, Color.RED)) +
                "  ".repeat(10 - Students.countColor(dining, Color.RED)) + "      |   " +
                (player.getOwnedProfessors().contains(Color.RED) ? (ConsoleColors.RED + Messages.PROF_CHAR + ConsoleColors.RESET) : " ") + "   |");

        // Third line (4th and 5th entrance students + yellow dining
        System.out.println("|   " +
                entranceHelper(entrance, 3) + "  " +
                entranceHelper(entrance, 4) + "   |   " +
                (getStringFromStudent(new Student(Color.YELLOW)) + " ").repeat(Students.countColor(dining, Color.YELLOW)) +
                "  ".repeat(10 - Students.countColor(dining, Color.YELLOW)) + "      |   " +
                (player.getOwnedProfessors().contains(Color.YELLOW) ? (ConsoleColors.YELLOW + Messages.PROF_CHAR + ConsoleColors.RESET) : " ") + "   |");

        // Fourth line (6th and 7th entrance students + pink dining
        System.out.println("|   " +
                entranceHelper(entrance, 5) + "  " +
                entranceHelper(entrance, 6) + "   |   " +
                (getStringFromStudent(new Student(Color.PINK)) + " ").repeat(Students.countColor(dining, Color.PINK)) +
                "  ".repeat(10 - Students.countColor(dining, Color.PINK)) + "      |   " +
                (player.getOwnedProfessors().contains(Color.PINK) ? (ConsoleColors.PURPLE + Messages.PROF_CHAR + ConsoleColors.RESET) : " ") + "   |");

        // Last line (8th and 9th students + blue dining
        System.out.println("|   " +
                entranceHelper(entrance, 7) + "  " +
                entranceHelper(entrance, 8) + "   |   " +
                (getStringFromStudent(new Student(Color.BLUE)) + " ").repeat(Students.countColor(dining, Color.BLUE)) +
                "  ".repeat(10 - Students.countColor(dining, Color.BLUE)) + "      |   " +
                (player.getOwnedProfessors().contains(Color.BLUE) ? (ConsoleColors.BLUE + Messages.PROF_CHAR + ConsoleColors.RESET) : " ") + "   |");


        System.out.println(DASHBOARD_LINE_50);
    }

    /**
     * Prints two dashboards side by side
     *
     * @param player1 the owner of the dashboard to be printed on the left
     * @param player2 the owner of the dashboard to be printed on the right
     */
    private static void printDoubleDashboard(PlayerState player1, PlayerState player2, boolean isExpert) {
        // TODO implement double dashboards
        printSingleDashboard(player1, isExpert);
        System.out.println();
        printSingleDashboard(player2, isExpert);
    }

    /**
     * Returns a string containing one printable character, which is the i-th student in the given entrance
     * or a string containing just a space char if the given index is out of bound
     *
     * @param entrance a list of students
     * @param i        an integer indicating the position of the student to get
     * @return a string containing exactly one printable character
     */
    private static String entranceHelper(List<Student> entrance, int i) {
        if (i >= entrance.size()) {
            return " ";
        }

        return getStringFromStudent(entrance.get(i));
    }

    /**
     * Counts the number of printed lines and if less than 47 prints void lines
     *
     * @param gameState the state of the game
     */
    private static void addEmptyLines(GameState gameState) {
        // TODO: check if correct
        int printedLines = 26;   // headers, dashboards and assistants are always printed
        printedLines += gameState.getIslands().size();      // one line per island
        printedLines += gameState.getClouds().size();
        printedLines += gameState.isExpert() ? 1 + gameState.getCharacters().size() : 0;    // one line per character + header

        while (printedLines < 47) {
            System.out.println();
            printedLines++;
        }
    }


    /**
     * Returns a String from a student.
     * The string is composed by a console color sequence, the student char and the console color reset sequence
     *
     * @param student a {@code Student}
     * @return a string containing only one printable char
     */
    public static String getStringFromStudent(Student student) {
        String consoleColor = "";
        switch (student.getColor()) {
            case GREEN -> consoleColor = ConsoleColors.GREEN;
            case PINK -> consoleColor = ConsoleColors.PURPLE;
            case RED -> consoleColor = ConsoleColors.RED;
            case BLUE -> consoleColor = ConsoleColors.BLUE;
            case YELLOW -> consoleColor = ConsoleColors.YELLOW;
        }

        return consoleColor + Messages.STUDENT_CHAR + ConsoleColors.RESET;
    }

    /**
     * Returns a String from a list of students
     *
     * @param students a {@code List} of students
     * @return a string
     */
    public static String getStringFromStudentList(List<Student> students) {
        if (students == null || students.isEmpty()) return "";

        StringBuilder stringBuilder = new StringBuilder();
        for (Student student : students) {
            stringBuilder.append(getStringFromStudent(student)).append(" ");
        }

        return stringBuilder.toString();
    }
}

