package it.polimi.ingsw.messages;

public class Message {
    private String status;
    private int error;

    public Message() {
        this.error = 0;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
