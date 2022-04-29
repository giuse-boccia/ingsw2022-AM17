package it.polimi.ingsw.messages.login;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class ClientActionMessage {

    public static ClientActionMessage getMessageFromJSON(String json) throws JsonSyntaxException {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<ClientActionMessage>() {
        }.getType());
    }
}
