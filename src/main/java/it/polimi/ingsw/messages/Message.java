package it.polimi.ingsw.messages;

public class Message {
    private String type;
    private int error;

    public Message() {
        this.error = 0;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
