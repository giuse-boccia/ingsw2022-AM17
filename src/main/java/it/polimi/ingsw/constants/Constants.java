package it.polimi.ingsw.constants;

public class Constants {
    // TODO move all "magic numbers" here

    // Server Constants
    public static final int PING_INTERVAL = 20000000;
    public static final int PONG_INITIAL_DELAY = 300000000;
    public static final int PONG_INTERVAL = 300000000;

    // Client Constants
    public static final int DEFAULT_PORT = 7373;    // to be used if no port is found in settings.json or given by args
    public static final String DEFAULT_ADDRESS = "localhost";

    // Model Constants
    public static final int INITIAL_STUDENTS_IN_BAG = 24;


}
