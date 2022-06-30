package it.polimi.ingsw.client.gui.utils;

import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.client.gui.controllers.ChatController;
import it.polimi.ingsw.languages.Messages;
import it.polimi.ingsw.messages.chat.ChatMessage;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatView {

    private static boolean isChatAlreadyOpened = false;
    private static ChatController chatController;

    /**
     * Shows the chat panel, if not already open
     */
    public static void openChatPanel() {
        if (isChatAlreadyOpened) return;
        isChatAlreadyOpened = true;
        FXMLLoader loader = new FXMLLoader(GuiView.class.getResource("/chat.fxml"));
        try {
            Scene scene = new Scene(loader.load(), 400, 400);
            chatController = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Chat");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setOnCloseRequest(event -> isChatAlreadyOpened = false);
            stage.show();

        } catch (IOException e) {
            GuiView.getGui().gracefulTermination(Messages.getMessage("generic_error_gui"));
        }
    }

    /**
     * Sends the received chat message to the correct controller
     *
     * @param message the received {@link ChatMessage}
     */
    public static void onChatMessageReceived(ChatMessage message) {
        if (!isChatAlreadyOpened) return;
        Platform.runLater(() -> chatController.onChatMessageReceived(message));
    }

}
