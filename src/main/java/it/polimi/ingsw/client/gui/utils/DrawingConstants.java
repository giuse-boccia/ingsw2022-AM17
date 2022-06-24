package it.polimi.ingsw.client.gui.utils;

public class DrawingConstants {
    public static final String FONT_NAME = "Baskerville Old Face";
    public static final int TITLE_FONT_SIZE = 18;
    public static final int SUBTITLE_FONT_SIZE = 16;
    public static final int PARAGRAPH_FONT_SIZE = 14;

    // Lobby Constants
    public static final double LOBBY_HGAP = 0.015625;
    public static final double LOBBY_VGAP = 0.015625;
    public static final double LOBBY_LEFT = 0.015625;
    public static final double LOBBY_RIGHT = 0.015625;
    public static final double LOBBY_TOP = 4.5;

    // Characters constants
    public static final double CHARACTER_CARD_PROPORTION = 0.06;
    public static final double CHARACTER_Y_DIVISOR = 13;
    public static final double SPACE_BETWEEN_CHARACTERS_PROPORTION = 0.005;
    public static final double ASSISTANT_AND_CHARACTERS_BEGINNING_PROPORTION = 0.035;
    public static final double SPACE_BETWEEN_STUDENTS_ON_CHARACTERS = 0.007;
    public static final double CHARACTER_COIN_DIM = 0.21;
    public static final double CHARACTER_STUDENT_DIM = 0.016;

    // Students constants
    public static final int STUDENT_DIMENSION_DIVISOR = 25;

    // Professor constants
    public static final int PROFESSOR_DIMENSION_DIVISOR = 20;
    public static final int PROFESSOR_ROTATION = 90;

    // Coin constants
    public static final double COIN_PROPORTION = 0.03;
    public static final double COIN_PROPORTION_SMALL = 0.015;
    public static final int COIN_DIMENSION_IN_TEXT_DIVISOR = 24;

    // Player constants
    public static final double PLAYER_NAME_INITIAL_PADDING = 0.05;

    // Clouds constants
    public static final double CLOUD_STARTING_Y = 0.485;
    public static final double CLOUD_STARTING_X = 0.73;
    public static final double SPACE_BETWEEN_CLOUDS = 0.006;
    public static final double CLOUD_HEIGHT = 0.16;
    public static final double X_OFFSET_OF_CLOUD_FROM_CENTER = 0.085;
    public static final double Y_OFFSET_OF_CLOUD_FROM_CENTER = 0.17;
    public static final double OFFSET_OF_STUDENT_FROM_CLOUD = 0.2;
    public static final double OFFSET_BETWEEN_STUDENTS_IN_CLOUD = 0.05;
    public static final double STUDENT_ON_CLOUD_DIMENSION = 0.02;


    // Assistants contants
    public static final double ASSISTANT_Y = 0.32;
    public static final double ASSISTANT_BASE_WIDTH = 80;
    public static final double OFFSET_BETWEEN_ASSISTANTS = -56;
    public static final double ASSISTANT_HOVERED_WIDTH = 112;

    // Dashboard constanst
    public static final double OTHER_DASHBOARD_HEIGHT = 0.17;
    public static final double MEGA_DASHBOARD_HEIGHT = 0.32;
    public static final double DASHBOARD_HEIGHT_OVER_WIDTH = 1454.0 / 3352.0;

    // Entrance constants
    public static final double ENTRANCE_HGAP = 0.018;
    public static final double ENTRANCE_VGAP = 0.03082;
    public static final double INITIAL_X_OFFSET_ENTRANCE = 0.034;
    public static final double INITIAL_Y_OFFSET_ENTRANCE = 0.052;

    // Dining room constants
    public static final double DINING_ROOM_HGAP = 0.00824;
    public static final double DINING_ROOM_VGAP = 0.03145;
    public static final double INITIAL_X_OFFSET_DINING_ROOM = 0.1875;
    public static final double INITIAL_Y_OFFSET_DINING_ROOM = 0.055;

    // Professor room constants
    public static final double INITIAL_X_OFFSET_PROFESSOR_ROOM = 0.705;
    public static final double INITIAL_Y_OFFSET_PROFESSOR_ROOM = 0.05;
    public static final double PROFESSOR_ROOM_VGAP = 0.0195;

    // Islands constants
    public static final double ISLAND_RADIUS = 0.4;
    public static final double ISLAND_DIMENSION = 0.12;
    public static final double ISLAND_X = 0.73;
    public static final double ISLAND_Y = 0.485;
    public static final double ISLAND_ELEMENTS_X = 0.019;
    public static final double ISLAND_ELEMENTS_Y = 0.025;
    public static final int ISLAND_TOWER_DIVISOR = 6;
    public static final int ISLAND_STUDENT_DIVISOR = 8;
    public static final int ISLAND_NOENTRY_DIVISOR = 8;
    public static final int ISLAND_MN_Y_DIVISOR = 3;
    public static final double ISLAND_MN_DIM = 0.3;

    // Towers constants
    public static final double INITIAL_X_OFFSET_TOWERS = 0.80515;
    public static final double INITIAL_Y_OFFSET_TOWERS = 0.075;
    public static final double TOWERS_HGAP = 0.01344;
    public static final double TOWERS_VGAP = 0.007548;
    public static final double TOWERS_SIZE = 0.065;

    // Character popups constants
    public static final int CHARACTER_POPUP_WIDTH = 400;
    public static final int CHARACTER_POPUP_HEIGHT = 210;
    public static final double CHARACTER_POPUP_TITLE_OFFSET_Y = 0.125;
    public static final double CHARACTER_POPUP_BUTTON_OFFSET_Y = 0.85;
    public static final double CHARACTER_POPUP_CHOICE_BOX_OFFSET_Y = 0.45;

    // End game popup
    public static final int END_GAME_POPUP_WIDTH = 350;
    public static final int END_GAME_POPUP_HEIGHT = 200;

    // IDs of elements
    public static final String ID_ROOT_GAME = "root";

    // Style Classes
    public static final String STYLE_HIGHLIGHT = "highlight_element";
    public static final String STYLE_SELECTED = "selected_element";
    public static final String STYLE_SWAP_CHARACTER_A = "element_active_for_swap_character";
    public static final String STYLE_SWAP_CHARACTER_S = "element_selected_for_swap_character";
    public static final String STYLE_MOVING_CHARACTER_A = "element_active_for_moving_character";
    public static final String STYLE_MOVING_CHARACTER_S = "element_selected_for_moving_character";
    public static final String STYLE_ISLAND_CHARACTER_A = "element_active_for_island_character";

    // Resources
    public static final String RESOURCE_PARAMETERS = "game_parameters";
    public static final String RESOURCE_LOBBY = "lobby";
    public static final String RESOURCE_LOGIN = "login";
    public static final String RESOURCE_CREATE_OR_LOAD_GAME = "create_or_load";

    // Character popup
    public static final double CHARACTER_HOVER_POPUP_WIDTH = 300;
}
