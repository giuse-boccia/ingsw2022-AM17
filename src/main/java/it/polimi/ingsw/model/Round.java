package it.polimi.ingsw.model;

import java.util.ArrayList;

public class Round {
    private final int firstPlayerIndex;
    private PlanningPhase planningPhase;
    private PlayerActionPhase currentPlayerActionPhase;

    public Round(int firstPlayerIndex) {
        this.firstPlayerIndex = firstPlayerIndex;
    }

    public PlanningPhase getPlanningPhase() {
        return planningPhase;
    }

    public PlayerActionPhase getCurrentPlayerActionPhase() {
        return currentPlayerActionPhase;
    }

    public int getFirstPlayerIndex() {
        return firstPlayerIndex;
    }
}
