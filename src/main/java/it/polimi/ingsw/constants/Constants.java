package it.polimi.ingsw.constants;

public class Constants {
    // TODO move all "magic numbers" here

    // Server Constants
    public static final int PING_INTERVAL = 2000;
    public static final int PONG_INITIAL_DELAY = 3000;
    public static final int PONG_INTERVAL = 2500;
    public static final int MAX_ATTEMPTS_TO_CONTACT_SERVER = 2;
    public static final int LOWER_BOUND_SERVER_PORT = 1024;
    public static final int UPPER_BOUND_SERVER_PORT = 65535;
    public static final String SAVED_GAME_PATH = "saved_game.json";

    // Client Constants
    public static final int DEFAULT_PORT = 7373;    // to be used if no port is found in settings.json or given by args
    public static final String DEFAULT_ADDRESS = "localhost";

    // Model Constants
    public static final int INITIAL_STUDENTS_IN_BAG_OF_EACH_COLOR = 24;
    public static final int ISLANDS = 12;
    public static final int STUDENTS_IN_ENTRANCE_IN_THREE_PLAYER_GAME = 9;
    public static final int STUDENTS_ON_CLOUD_IN_THREE_PLAYER_GAME = 4;
    public static final int STUDENTS_TO_MOVE_IN_THREE_PLAYER_GAME = 4;
    public static final int STUDENTS_IN_ENTRANCE_IN_TWO_OR_FOUR_PLAYER_GAME = 7;
    public static final int STUDENTS_ON_CLOUD_IN_TWO_OR_FOUR_PLAYER_GAME = 3;
    public static final int STUDENTS_TO_MOVE_IN_TWO_OR_FOUR_PLAYER_GAME = 3;
    public static final int MN_BONUS = 2;
    public static final int INFLUENCE_BONUS = 2;


}
