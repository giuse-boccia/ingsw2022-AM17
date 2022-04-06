package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

import java.util.ArrayList;

public class Login {

    private final ArrayList<Player> players;
    private int desiredNumberOfPlayers;

    public Login() {
        players = new ArrayList<>();
    }

    public void addPlayer(String name) {
        Player newPlayer = new Player(name, desiredNumberOfPlayers == 3 ? 8 : 10);
    }

}
