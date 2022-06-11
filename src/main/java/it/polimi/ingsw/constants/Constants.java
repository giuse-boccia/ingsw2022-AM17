package it.polimi.ingsw.constants;

public class Constants {
    // TODO move all "magic numbers" here
    // Math Constants
    public static final double PI = 3.14;

    // Server Constants
    public static final int PING_INTERVAL = 2000;
    public static final int PONG_INITIAL_DELAY = 3000;
    public static final int PONG_INTERVAL = 2500;
    public static final int MAX_ATTEMPTS_TO_CONTACT_SERVER = 2;
    public static final int MAX_USERNAME_LENGTH = 16;
    public static final int LOWER_BOUND_SERVER_PORT = 1024;
    public static final int UPPER_BOUND_SERVER_PORT = 65535;
    public static final String SAVED_GAME_PATH = "saved_game.json";
    public static final String ERIANTYS = "Eriantys";
    public static final String SETTINGS_PATH = "./settings.json";

    // Client Constants
    public static final int DEFAULT_PORT = 7373;    // to be used if no port is found in settings.json or given by args
    public static final String DEFAULT_ADDRESS = "localhost";

    // Model Constants
    public static final int INITIAL_STUDENTS_IN_BAG_OF_EACH_COLOR = 24;
    public static final int STUDENTS_IN_ENTRANCE_IN_THREE_PLAYER_GAME = 9;
    public static final int STUDENTS_ON_CLOUD_IN_THREE_PLAYER_GAME = 4;
    public static final int STUDENTS_TO_MOVE_IN_THREE_PLAYER_GAME = 4;
    public static final int STUDENTS_IN_ENTRANCE_IN_TWO_OR_FOUR_PLAYER_GAME = 7;
    public static final int STUDENTS_ON_CLOUD_IN_TWO_OR_FOUR_PLAYER_GAME = 3;
    public static final int STUDENTS_TO_MOVE_IN_TWO_OR_FOUR_PLAYER_GAME = 3;
    public static final int TOWERS_IN_TWO_OR_FOUR_PLAYER_GAME = 8;
    public static final int TOWES_IN_THREE_PLAYER_GAME = 6;
    public static final int MN_BONUS = 2;
    public static final int INFLUENCE_BONUS = 2;
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 4;
    public static final int MAX_ISLANDS = 12;
    public static final int MAX_MN_STEPS = 15;
    public static final int MAX_CLOUDS = 4;
    public static final int NUM_CHARACTERS = 3;
    public static final int MIN_ASSISTANT_VALUE = 1;
    public static final int MAX_ASSISTANT_VALUE = 10;
    public static final int MAX_STUDENTS_IN_DINING = 10;

    // Gui constants
    public static final int TOAST_DURATION_SECONDS = 4;


}
