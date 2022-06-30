package it.polimi.ingsw.languages;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessagesForClients {

    /**
     * Returns the message corresponding to the input key in the corresponding language
     *
     * @param key    the key to find the corresponding value of
     * @param locale the {@code Locale} to get the language from
     * @return the message corresponding to the input key in the corresponding language
     */
    public static String getMessage(String key, Locale locale) {
        ResourceBundle messages = ResourceBundle.getBundle("messages/messages", locale);
        return messages.getString(key);
    }
}
