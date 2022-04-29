package it.polimi.ingsw;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class Settings {

    private final int port;
    private final String address;

    private Settings(int port, String address) {
        this.port = port;
        this.address = address;
    }

    public static Settings readPrefsFromFile() throws IOException, NumberFormatException {
        Gson gson = new Gson();

        Reader reader = Files.newBufferedReader(Paths.get("./settings.json"));
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> map = gson.fromJson(reader, type);
        reader.close();
        int port = Integer.parseInt(map.get("server_port"));
        String address = map.get("server_address");
        return new Settings(port, address);
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }
}
