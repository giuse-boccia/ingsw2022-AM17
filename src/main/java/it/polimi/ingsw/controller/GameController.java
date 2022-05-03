package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.server.PlayerClient;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private final Game game;
    private final List<PlayerClient> players;

    public GameController(List<PlayerClient> players, boolean expert) {
        game = new Game(createPlayers(players), expert);
        this.players = players;
    }

    private ArrayList<Player> createPlayers(List<PlayerClient> players) {
        List<Player> playersList = players.stream().map(PlayerClient::getPlayer).toList();
        return new ArrayList<>(playersList);
    }

}
