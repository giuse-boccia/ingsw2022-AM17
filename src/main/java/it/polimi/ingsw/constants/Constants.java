package it.polimi.ingsw.constants;

public class Constants {
    // TODO move all "magic numbers" here

    // Server Constants
    public static final int PING_INTERVAL = 2000;
    public static final int PONG_INITIAL_DELAY = 3000;
    public static final int PONG_INTERVAL = 3000;

    // Client Constants
    public static final int DEFAULT_PORT = 7373;    // to be used if no port is found in settings.json or given by args
    public static final String DEFAULT_ADDRESS = "localhost";

    // Model Constants
    public static final int INITIAL_STUDENTS_IN_BAG = 24;


}
