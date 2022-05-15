package it.polimi.ingsw.controller;

import it.polimi.ingsw.server.Communicable;

class ClientHandlerStub implements Communicable {

    private String json;

    @Override
    public void sendMessageToClient(String json) {
        // This is a stub: the message isn't sent to client
        this.json = json;
    }

    public String getJson() {
        return json;
    }
}
