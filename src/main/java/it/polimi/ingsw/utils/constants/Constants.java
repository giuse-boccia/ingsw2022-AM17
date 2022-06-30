package it.polimi.ingsw.utils.constants;

import java.util.List;

public class Constants {
    // Math Constants
    public static final double PI = 3.14;

    // Server Constants
    public static final int PING_INTERVAL = 5000;
    public static final int PONG_INITIAL_DELAY = 5500;
    public static final int PONG_INTERVAL = 5500;
    public static final int MAX_ATTEMPTS_TO_CONTACT_SERVER = 2;
    public static final int MAX_USERNAME_LENGTH = 32;
    public static final int LOWER_BOUND_SERVER_PORT = 1024;
    public static final int UPPER_BOUND_SERVER_PORT = 65535;
    public static final String STUDENT_CHAR = "*";
    public static final String PROF_CHAR = "#";
    public static final String SAVED_GAME_PATH = "saved_game.json";
    public static final String ERIANTYS = "Eriantys";
    public static final String SETTINGS_PATH = "./settings.json";
    public static final String SERVER_PORT = "server_port";
    public static final String SERVER_ADDRESS = "server_address";
    public static final String GAME_LANGUAGE = "game_language";

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
    public static final int TOWERS_IN_THREE_PLAYER_GAME = 6;
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
    public static final int TOAST_DURATION_SECONDS = 6;

    // Actions
    public static final String ACTION_SET_USERNAME = "SET_USERNAME";
    public static final String ACTION_CREATE_GAME = "CREATE_GAME";
    public static final String ACTION_LOAD_GAME = "LOAD_GAME";
    public static final String ACTION_PLAY_ASSISTANT = "PLAY_ASSISTANT";
    public static final String ACTION_MOVE_STUDENT = "MOVE_STUDENT";
    public static final String ACTION_MOVE_STUDENT_TO_DINING = "MOVE_STUDENT_TO_DINING";
    public static final String ACTION_MOVE_STUDENT_TO_ISLAND = "MOVE_STUDENT_TO_ISLAND";
    public static final String ACTION_MOVE_MN = "MOVE_MN";
    public static final String ACTION_PLAY_CHARACTER = "PLAY_CHARACTER";
    public static final String ACTION_FILL_FROM_CLOUD = "FILL_FROM_CLOUD";

    // Possible status in messages
    public static final String STATUS_LOGIN = "LOGIN";
    public static final String STATUS_ACTION = "ACTION";
    public static final String STATUS_PONG = "PONG";
    public static final String STATUS_PING = "PING";
    public static final String STATUS_UPDATE = "UPDATE";
    public static final String STATUS_CHAT = "CHAT";
    public static final String STATUS_END = "END";

    // Chat-related constants
    public static final int MAX_LENGTH_FOR_MESSAGE = 200;
    public static final String ACTION_SEND_MESSAGE = "SEND_MESSAGE";
    public static final String ACTION_GET_ALL_MESSAGES = "GET_ALL_MESSAGES";
    public static final String ACTION_SEND_ALL_MESSAGES = "SEND_ALL_MESSAGES";

    // Possible language tags
    public static final List<String> LANGUAGE_TAGS = List.of("en", "it");

}
