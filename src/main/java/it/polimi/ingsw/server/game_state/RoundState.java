package it.polimi.ingsw.server.game_state;

import it.polimi.ingsw.model.game_objects.Assistant;

import java.util.List;

public class RoundState {
    /**
     * a {@code List} of all the assistants played during the player
     */
    private List<Assistant> playedAssistants;

    private int status;
    private boolean isLastRound;

    /*
    "round": {
        "status": -1 -
        "next_player": 1
    }
    */
}
