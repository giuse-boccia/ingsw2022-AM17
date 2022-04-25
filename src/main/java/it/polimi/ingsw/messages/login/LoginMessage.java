package it.polimi.ingsw.messages.login;

import it.polimi.ingsw.messages.Message;

public class LoginMessage extends Message {
    private String message;
    private String action;
    private LoginError loginError;
    private GameLobby gameLobby;

    public LoginMessage() {
        super.setType("login");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LoginError getLoginError() {
        return loginError;
    }

    public void setLoginError(LoginError loginError) {
        this.loginError = loginError;
    }

    public GameLobby getGameLobby() {
        return gameLobby;
    }

    public void setGameLobby(GameLobby gameLobby) {
        this.gameLobby = gameLobby;
    }
}


