package it.polimi.ingsw;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Key;
import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.languages.MessageResourceBundle;

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

    /**
     * Reads the connections settings from a file
     *
     * @return the {@code Settings} read from a file
     * @throws NumberFormatException if the server_port field in not a number
     */
    public static Settings readPrefsFromFile() throws IOException, NumberFormatException {
        Map<String, String> map = openFile();
        int port = Integer.parseInt(map.get(Constants.SERVER_PORT));
        String address = map.get(Constants.SERVER_ADDRESS);
        return new Settings(port, address);
    }

    /**
     * Returns the game language from the settings.json file
     *
     * @return the game language from the settings.json file
     */
    public static String getGameLanguage() throws IOException {
        Map<String, String> map = openFile();
        String gameLanguage = map.get(Constants.GAME_LANGUAGE);
        if (gameLanguage == null || gameLanguage.length() != 2)
            return "en";
        return gameLanguage;
    }

    /**
     * Opens the settings.json file
     *
     * @return a {@code Map} containing the different fields and values of the file
     */
    private static Map<String, String> openFile() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(Constants.SETTINGS_PATH));
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> map = gson.fromJson(reader, type);
        reader.close();
        return map;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }
}
