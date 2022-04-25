package it.polimi.ingsw.messages;

public abstract class Message {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
