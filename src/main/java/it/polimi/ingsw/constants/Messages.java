package it.polimi.ingsw.constants;

public class Messages {
    public static final String ASK_USERNAME = "Insert username: ";
    public static final String GRACEFUL_TERM = "Application will now close...";

    public static final String STUDENT_CHAR = "●";     // in Windows Terminal is '?' unless you run "chcp 65001" beforehand
    public static final String PROF_CHAR = "▲";
    public static final String NO_ENTRY_CHAR = "X";


    // Possible status in messages
    public static final String STATUS_LOGIN = "LOGIN";
    public static final String STATUS_ACTION = "ACTION";
    public static final String STATUS_UPDATE = "UPDATE";
    public static final String STATUS_END = "END";

    // Generic errors
    public static final String INVALID_IDENTITY = "You are not who you are pretending to be";

    // Errors while joining game
    public static final String INVALID_USERNAME = "Invalid username field";
    public static final String USERNAME_TOO_LONG = "Username is too long (max 32 characters)";
    public static final String USERNAME_ALREADY_TAKEN = "Username is too long (max 32 characters)";
    public static final String LOBBY_FULL = "The lobby is full";
    public static final String INVALID_NUM_PLAYERS = "Num players must be between 2 and 4";
    public static final String INVALID_PLAYER_CREATING_GAME = "You can't set this game's parameters";
    public static final String INVALID_FORMAT_NUM_PLAYER = "Num players must be a number";

    // Messages in game creation
    public static final String ADDED_PLAYER = "Added player ";
    public static final String SET_GAME_PARAMETERS = "Added player ";
    public static final String NEW_PLAYER_JOINED = "A new player has joined";
    public static final String GAME_STARTING = "A new game is starting";
    public static final String GAME_CREATED = "A new game was created!";

    // Errors in action phase
    public static final String GAME_NOT_STARTED = "Game is not started yet";
    public static final String NOT_YOUR_TURN = "It's not your turn";
    public static final String INVALID_ARGUMENT = "Invalid argument";
    public static final String ALREADY_PLAYED_ASSISTANT = "You have already played this assistant";
    public static final String ANOTHER_PLAYED_ASSISTANT = "Someone already played the same assistant this turn";
    public static final String NOT_LOGGED_IN = "You are not logged in!";
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String INVALID_CLOUD = "The selected cloud is not valid";
    public static final String INVALID_MN_MOVE = "Invalid move for mother nature";
    public static final String MOVE_MN_FIRST = "Move mother nature first";
    public static final String ENTRANCE_DOESNT_CONTAIN_STUDENT = "The entrance doesn't contain this student";
}
