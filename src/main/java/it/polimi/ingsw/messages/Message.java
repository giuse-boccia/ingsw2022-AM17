package it.polimi.ingsw.messages;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.login.ServerLoginMessage;

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

    /**
     * Returns a {@code Message} object from a Json {@code String}
     *
     * @param json the Json {@code String}
     * @return a {@code Message} object from a Json {@code String}
     */
    public static Message fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<Message>() {
        }.getType());
    }

    /**
     * Returns a Json {@code String} from a {@code Message}
     *
     * @return a Json {@code String} from a {@code Message}
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
