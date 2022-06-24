package it.polimi.ingsw.languages;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageResourceBundle {

    private static ResourceBundle messages;

    /**
     * Returns the message corresponding to the input key
     *
     * @param key the key to find the corresponding value of
     * @return the message corresponding to the input key
     */
    public static String getMessage(String key) {
        // Send the message in the selected language, if present
        if (messages.containsKey(key)) {
            return messages.getString(key);
        }
        // Otherwise, return the message in english
        ResourceBundle englishMessages = ResourceBundle.getBundle("messages/messages", Locale.ENGLISH);
        return englishMessages.getString(key);
    }

    /**
     * Sets up the {@code ResourceBundle} according to the given language tag
     *
     * @param tag the {@code String} representing the code of the desired language
     */
    public static void initializeBundle(String tag) {
        if (tag == null || (!tag.equals("en") && !tag.equals("it"))) {
            tag = "en";
        }
        Locale currentLocale = new Locale.Builder().setLanguageTag(tag).build();
        messages = ResourceBundle.getBundle("messages/messages", currentLocale);
    }

}
