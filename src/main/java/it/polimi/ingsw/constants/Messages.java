package it.polimi.ingsw.constants;

public class Messages {

    // Possible status in messages
    public static final String STATUS_LOGIN = "LOGIN";
    public static final String STATUS_ACTION = "ACTION";
    public static final String STATUS_PONG = "PONG";
    public static final String STATUS_PING = "PING";
    public static final String STATUS_UPDATE = "UPDATE";
    public static final String STATUS_END = "END";

    public static final String GAME_CREATED = "A new game was created!";
    public static final String GAME_LOADED = "A saved game was loaded!";
    public static final String SERVER_READY = "Server ready, waiting for clients...";
    public static final String NEW_SOCKET = "New socket: ";
    public static final String NO_GAME_RUNNING = "No game on server ";
    public static final String CREATE_GAME = "1. Create a new game";
    public static final String LOAD_GAME = "2. Load a previous game";
    public static final String ASK_NUM_PLAYERS = "Insert desired number of players (2, 3 or 4): ";
    public static final String ASK_EXPERT = "Do you want to play in expert mode? [Y/n] ";

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
    public static final String MOVE_STUDENTS_FIRST = "Move your students first";
    public static final String ENTRANCE_DOESNT_CONTAIN_STUDENT = "The entrance doesn't contain this student";
    public static final String STUDENT_NOT_IN_ENTRANCE = "One or more students are not on the entrance";
    public static final String DINING_ROOM_DOESNT_CONTAIN_STUDENT = "The dining room doesn't contain this student";
    public static final String STUDENT_NOT_IN_DINING_ROOM = "One or more students are not on the dining room";
    public static final String NO_CHARACTER_IN_NON_EXPERT = "There are no characters in the non expert mode";
    public final static String INVALID_CHARACTER = "This is not a valid character";
    public static final String CHARACTER_NOT_IN_GAME = "This character is not in the game";
    public static final String INVALID_SWAP = "This is not a valid swap";
    public static final String MOVE_JUST_ONE = "You have to move only one student";
    public static final String STUDENT_NOT_FOUND = "Student not found. Check your choices";
    public static final String STUDENT_NOT_ON_CHARACTER = "The character doesn't contain this student";
    public static final String NO_NOENTRY = "There are no NoEntry pawns left on this card";
    public static final String NOT_PASSIVE = "This is not a passive character";
    public static final String MOVING_MORE_STUDENTS = "You are trying to move more students than you are allowed to";
    public static final String ALREADY_PLAYED_CHARACTER = "You already played a character this turn";
    public static final String EMPTY_BAG = "The bag is empty!";
    public static final String NOT_ENOUGH_COINS = "You don't have enough coins to play this character";
    public static final String INVALID_ISLAND = "Invalid island index";
    public static final String ACTION_MUST_NUMBER = "Action identifier must be a number!";
    public static final String MUST_NUMBER = " must be a number!";
    public static final String POSSIBLE_COLORS = "Color not recognised [blue | green | yellow | red | pink]";

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

    // Messages in planning phase
    public static final String PLANNING_PHASE_ENDED = "Planning phase is ended";
    public static final String ASK_ASSISTANT = "Insert value of the selected assistant [1-10]: ";
    public static final String NO_ASSISTANT_LEFT = "No assistants left";

    // Messages in action phase
    public static final String LAST_ROUND = "Be aware! This is the last round";
    public static final String GAME_ENDED = "The game is ended";
    public static final String GAME_WON = "Congratulations, you won the game!";
    public static final String GAME_LOST = "Unfortunately you lost.";
    public static final String IS_PLAYING = " is playing...";
    public static final String ASK_CHARACTER = "Insert the number of the character you want to play [1-3]: ";
    public static final String ASK_MN = "Insert number of steps of mother nature: ";
    public static final String ASK_CLOUD = "Insert number of the cloud you want to pick: ";
    public static final String ASK_ISLAND = "Insert number of the selected island: ";
    public static final String ASK_NUM_STUDENTS = "Choose how many students you want to swap: ";
    public static final String ASK_COLOR = "Write the color of the student: ";
    public static final String POSSIBLE_ACTIONS = "You have the following actions: ";
    public static final String SELECT_NUMBER = "Select the corresponding number: ";

    // Server log messages
    public static final String GAME_IS_STARTING = "Game is starting";
    public static final String SAVE_OK = "Game saved to file";
    public static final String SAVE_ERR = "Error while saving the game";
    public static final String LOAD_OK = "Game loaded from file";
    public static final String LOAD_ERR = "Error while loading the game";
    public static final String SAVING_TO_FILE = "Saving to file...";
    public static final String CLEARING_GAME = "Connection with one client lost, clearing the game...";

    // Errors for GUI
    public static final String GENERIC_ERROR_GUI = "Error while loading the scene";
    public static final String GUI_ERROR = "Error";

    // Character messages
    public static final String CHOOSE_BOUND_CHARACTER = "Choose how many students do you want to swap";
    public static final String CHOOSE_COLOR_CHARACTER = "Choose one color for the effect of the character";

    // Messages in GUI
    public static final String END_GAME_TITLE = "Game is ended";
    public static final String END_GAME_BUTTON_TEXT = "Close application";
    public static final String GAME_ENDED_MESSAGE = "The game is ended. See you at the next match!";
    public static final String CONFIRM = "Confirm";

    // Broadcast messages to notify that a player did a specific action
    public static final String BROADCAST_SEPARATOR = ". Now ";
    public static final String BROADCAST_ASSISTANT = " played assistant number ";
    public static final String BROADCAST_TO_DINING = " student to his dining room";
    public static final String BROADCAST_TO_ISLAND = " student to island number ";
    public static final String BROADCAST_FOR_MOTHER_NATURE = " moved mother nature of ";
    public static final String BROADCAST_FILL_FROM_CLOUD = " chose cloud number ";
    public static final String BROADCAST_GAME_WON = " won the game";

    // Alert messages in GUI
    public static final String CLOSE_GAME_TITLE = "Close game";
    public static final String CONFIRM_CLOSE_GAME = "Are you sure you want to close Eriantys?";
}
