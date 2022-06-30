package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.messages.chat.ChatMessage;
import it.polimi.ingsw.utils.constants.Constants;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChatController {


    public TextField messageTextField;
    public Button btnSendMessage;
    public ScrollPane messagesScrollPane;
    public VBox vBoxMessages;


    @FXML
    void initialize() {
        // Asks all the messages to the server
        GuiView.getGui().getCurrentObserverHandler().notifyAllRequestMessagesObservers();

        messageTextField.setOnKeyTyped(event -> {
            String text = messageTextField.getText();
            if (text.length() > Constants.MAX_LENGTH_FOR_MESSAGE) {
                messageTextField.setText(text.substring(0, Constants.MAX_LENGTH_FOR_MESSAGE));
                messageTextField.positionCaret(Constants.MAX_LENGTH_FOR_MESSAGE);
            }
        });

        btnSendMessage.setOnMouseClicked(event -> {
            String toSend = messageTextField.getText();
            if (!toSend.isBlank()) {
                vBoxMessages.getChildren().add(getLayoutForSentMessage(toSend));
                messagesScrollPane.setVvalue(1.0);
                messageTextField.setText("");
                // Send message to server
                GuiView.getGui().getCurrentObserverHandler()
                        .notifyAllChatMessageObservers(new ChatMessage(toSend, GuiView.getGui().getUsername()));
            }
        });

        vBoxMessages.heightProperty().addListener((obs, oldVal, newVal) -> {
            messagesScrollPane.setVvalue((Double) newVal);
        });
    }

    /**
     * Returns a {@code HBox} containing the layout for the messages sent by the user
     *
     * @param message the {@code String} sent from the user to other players
     * @return a {@code HBox} containing the layout for the messages sent by the user
     */
    private HBox getLayoutForSentMessage(String message) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5));
        Text text = new Text(message);
        text.setFill(Color.BLACK);
        text.setStyle("-fx-font-size: 15");
        text.setWrappingWidth(280);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #ea95e2; -fx-background-radius: 20px; -fx-border-radius: 10");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);
        return hBox;
    }

    /**
     * Returns a {@code HBox} containing the layout for the messages received by the user
     *
     * @param message the received message
     * @param sender  the sender of the showed message
     * @return a {@code HBox} containing the layout for the messages received by the users
     */
    private HBox getLayoutForReceivedMessage(String message, String sender) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5));
        Text messageText = new Text("> " + message);
        messageText.setFill(Color.BLACK);
        messageText.setStyle("-fx-font-size: 15");
        messageText.setWrappingWidth(280);
        Text senderText = new Text(sender);
        senderText.setFill(Color.BLACK);
        senderText.setStyle("-fx-font-size: 10");
        VBox vBox = new VBox(senderText, messageText);
        vBox.setStyle("-fx-background-color: rgba(223,225,223,0.89); -fx-background-radius: 20px; -fx-border-radius: 10");
        vBox.setPadding(new Insets(5, 10, 5, 10));
        vBox.setSpacing(5);
        hBox.getChildren().add(vBox);
        return hBox;
    }

    /**
     * Adds the received message to the screen
     *
     * @param chatMessage the received {@link ChatMessage}
     */
    public void onChatMessageReceived(ChatMessage chatMessage) {
        HBox toAdd;
        if (chatMessage.getSender().equals(GuiView.getGui().getUsername())) {
            toAdd = getLayoutForSentMessage(chatMessage.getMessage());
        } else {
            toAdd = getLayoutForReceivedMessage(chatMessage.getMessage(), chatMessage.getSender());
        }
        vBoxMessages.getChildren().add(toAdd);
    }
}
