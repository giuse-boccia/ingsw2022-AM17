package it.polimi.ingsw.languages;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageResourceBundle {

    private static ResourceBundle messages;

    private static void setLanguage(String tag) {
        Locale currentLocale = new Locale.Builder().setLanguageTag(tag).build();
        messages = ResourceBundle.getBundle("messages/messages", currentLocale);
    }

    public static String getMessage(String key) {
        // Send the message in the selected language, if present
        if (messages.containsKey(key)) {
            return messages.getString(key);
        }
        // Otherwise, return the message in english
        ResourceBundle englishMessages = ResourceBundle.getBundle("messages/messages", Locale.ENGLISH);
        return englishMessages.getString(key);
    }

    public static void initializeBundle(String tag) {
        if (tag == null) {
            tag = "en";
        }
        setLanguage(tag);
    }

}
