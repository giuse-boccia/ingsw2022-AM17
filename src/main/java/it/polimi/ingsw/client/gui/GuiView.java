package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.controllers.ActionController;
import it.polimi.ingsw.client.gui.controllers.GuiController;
import it.polimi.ingsw.client.gui.utils.DrawingComponents;
import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
import it.polimi.ingsw.utils.constants.Constants;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.server.game_state.GameState;
import it.polimi.ingsw.utils.constants.Paths;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class GuiView extends Application {

    private static GUI gui;
    private static Scene scene;
    private static Stage stage;
    private static GuiController currentController;
    private static String currentSceneName;

    public static void main(GUI inputGui) {
        gui = inputGui;
        launch();
    }

    public static GUI getGui() {
        return gui;
    }

    /**
     * Shows a pop-up window for the user to choose a color or the number of students to swap/move
     *
     * @param bound the maximum number of students to move | -1 if the user has to choose a color
     */
    public static void showPopupForColorOrBound(int bound) {
        Stage popupStage = getNewUndecoratedStage();
        double width = DrawingConstants.CHARACTER_POPUP_WIDTH;
        double height = DrawingConstants.CHARACTER_POPUP_HEIGHT;

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(width, height);

        String titleText = bound == -1 ? MessageResourceBundle.getMessage("choose_color_character") : MessageResourceBundle.getMessage("choose_bound_character");
        Text title = new Text(titleText);
        title.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.SUBTITLE_FONT_SIZE));
        StackPane titleStackPane = new StackPane(title);
        titleStackPane.setPrefWidth(width);
        titleStackPane.setLayoutY(height * DrawingConstants.CHARACTER_POPUP_TITLE_OFFSET_Y);
        anchorPane.getChildren().add(titleStackPane);

        Button confirmBtn = new Button(MessageResourceBundle.getMessage("confirm"));
        confirmBtn.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.SUBTITLE_FONT_SIZE));
        StackPane confirmStackPane = new StackPane(confirmBtn);
        confirmStackPane.setPrefWidth(width);
        confirmStackPane.setLayoutY(height * DrawingConstants.CHARACTER_POPUP_BUTTON_OFFSET_Y);
        anchorPane.getChildren().add(confirmStackPane);

        if (bound == -1) {
            ObservableList<Color> colors = FXCollections.observableArrayList(Color.values());
            ChoiceBox<Color> colorChoiceBox = new ChoiceBox<>(colors);
            StackPane choiceBoxStackPane = new StackPane(colorChoiceBox);
            choiceBoxStackPane.setLayoutY(height * DrawingConstants.CHARACTER_POPUP_CHOICE_BOX_OFFSET_Y);
            choiceBoxStackPane.setPrefWidth(width);
            anchorPane.getChildren().add(choiceBoxStackPane);
            confirmBtn.setOnMouseClicked(event -> {
                gui.getCurrentObserverHandler().notifyPlayCharacterObservers(
                        ObjectClickListeners.getLastCharacterPlayed(), colorChoiceBox.getValue(), null, null, null
                );
                popupStage.close();
            });
        } else {
            ObservableList<Integer> values = FXCollections.observableArrayList(IntStream.rangeClosed(1, bound).boxed().toList());
            ChoiceBox<Integer> valuesChoiceBox = new ChoiceBox<>(values);
            StackPane choiceBoxStackPane = new StackPane(valuesChoiceBox);
            choiceBoxStackPane.setLayoutY(height * DrawingConstants.CHARACTER_POPUP_CHOICE_BOX_OFFSET_Y);
            choiceBoxStackPane.setPrefWidth(width);
            anchorPane.getChildren().add(choiceBoxStackPane);
            confirmBtn.setOnMouseClicked(event -> {
                DrawingComponents.removeGoldenBordersFromAllElements();
                ObjectClickListeners.setSwapCharacterPlayed(valuesChoiceBox.getValue());
                popupStage.close();
            });
        }

        popupStage.setScene(new Scene(anchorPane, width, height));
        popupStage.show();
    }

    /**
     * Returns an undecorated {@code Stage}
     *
     * @return an undecorated {@code Stage}
     */
    private static Stage getNewUndecoratedStage() {
        Stage newStage = new Stage();
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(scene.getWindow());
        newStage.initStyle(StageStyle.UNDECORATED);
        return newStage;
    }

    /**
     * Changes the scene showed on the gui
     *
     * @param resourceName the name of the scene to show (part of the .fxml file path)
     * @param fullscreen   true if the scene has to be in full screen mode
     */
    public void changeScene(String resourceName, boolean fullscreen) {
        if (Objects.equals(currentSceneName, resourceName)) {
            return;
        }

        currentSceneName = resourceName;
        FXMLLoader fxmlLoader = new FXMLLoader(GuiView.class.getResource("/" + resourceName + ".fxml"));
        Platform.runLater(() -> {
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                gui.gracefulTermination(MessageResourceBundle.getMessage("server_lost"));
            }
            stage.setResizable(fullscreen);
            stage.setFullScreen(false);
            stage.setScene(scene);
            currentController = fxmlLoader.getController();
        });
    }

    /**
     * Draws the scene at the start of the game
     */
    public void startGameScene() {
        Platform.runLater(() -> {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            double screenWidth = bounds.getWidth();
            double screenHeight = bounds.getHeight();
            AnchorPane root = new AnchorPane();
            root.setId(DrawingConstants.ID_ROOT_GAME);
            scene = new Scene(root, screenWidth, screenHeight);
            scene.getStylesheets().add(Paths.STYLE);

            ActionController newController = new ActionController();
            newController.setRoot(root);
            newController.initialize();
            currentController = newController;

            stage.setResizable(true);
            stage.setMaximized(true);
            stage.setScene(scene);
        });
    }

    /**
     * Sends a message to the controller containing the current {@code GameLobby}, {@code GameState}, list of action for the player to choose from and the username of the player
     *
     * @param lobby     the current {@code GameLobby} - null if not needed
     * @param gameState the current {@code GameState} - null if not needed
     * @param actions   the list of actions for the player to choose from
     * @param username  the name of the player
     */
    public void sendMessageToController(GameLobby lobby, GameState gameState, List<String> actions, String username) {
        Platform.runLater(() -> currentController.receiveData(lobby, gameState, actions, username));
    }

    /**
     * Calls a controller method to ask the user the parameters to play the selected character
     *
     * @param name          the name of the character to play
     * @param characterType the {@code GuiCharacterType} of the selected character
     */
    public void askCharacterParameters(CharacterName name, GuiCharacterType characterType) {
        Platform.runLater(() -> currentController.askCharacterParameters(name, characterType));
    }

    /**
     * Shows a pop-up window with an error message
     *
     * @param message          the message to be shown
     * @param closeApplication true if the application has to be closed
     */
    public void showErrorDialog(String message, boolean closeApplication) {
        // If there's a connection error the app should be closed without showing the alert - also to avoid
        // showing the login screen when the app can't start
        if (Objects.equals(message, MessageResourceBundle.getMessage("wrong_usage")) || Objects.equals(message, MessageResourceBundle.getMessage("json_not_found"))
                || Objects.equals(message, MessageResourceBundle.getMessage("invalid_server_port")) || Objects.equals(message, MessageResourceBundle.getMessage("port_not_available"))
                || Objects.equals(message, MessageResourceBundle.getMessage("cannot_connect_to_server"))) {
            closeAppWithErrorMessage(message);
            return;
        }
        Alert.AlertType alertType = closeApplication ? Alert.AlertType.ERROR : Alert.AlertType.WARNING;

        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(MessageResourceBundle.getMessage("gui_error"));
            alert.setHeaderText(message);
            alert.setContentText(null);
            alert.initOwner(stage);

            alert.showAndWait();

            if (closeApplication) {
                closeAppWithErrorMessage(message);
            }
        });
    }

    /**
     * Closes the application showing the given message
     *
     * @param message the message to show
     */
    private void closeAppWithErrorMessage(String message) {
        System.out.println(message);
        System.out.println(MessageResourceBundle.getMessage("graceful_term"));
        System.exit(-1);
    }

    /**
     * Shows a window asking the user if they are sure to close the game
     */
    private void confirmCloseApp() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(MessageResourceBundle.getMessage("close_game_title"));
            alert.setHeaderText(MessageResourceBundle.getMessage("confirm_close_game"));

            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                stage.close();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    /**
     * Shows a toast (a small pop-up window) at the bottom of the screen with a message
     *
     * @param message the message to show
     */
    public void showToast(String message) {
        if (scene == null) return;
        Platform.runLater(() ->
                Notifications.create().owner(stage).text(message)
                        .hideAfter(Duration.seconds(Constants.TOAST_DURATION_SECONDS))
                        .position(Pos.BOTTOM_CENTER)
                        .show()
        );
    }

    /**
     * Ends the game showing a message
     *
     * @param message the message to be shown
     */
    public void endGame(String message) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(MessageResourceBundle.getMessage("end_game_title"));

            Text endGameText = new Text(message);
            endGameText.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.SUBTITLE_FONT_SIZE));
            Button closeAppBtn = new Button();
            closeAppBtn.setText(MessageResourceBundle.getMessage("end_game_button_text"));
            closeAppBtn.setStyle("-fx-font-family: Algerian; -fx-font-size: 16");
            closeAppBtn.setOnMouseClicked(event -> {
                System.out.println(MessageResourceBundle.getMessage("game_ended_message"));
                System.exit(0);
            });
            VBox vBox = new VBox(endGameText, closeAppBtn);
            vBox.setAlignment(Pos.CENTER);

            stage.setScene(new Scene(vBox, DrawingConstants.END_GAME_POPUP_WIDTH, DrawingConstants.END_GAME_POPUP_HEIGHT));
            stage.show();
        });
    }


    @Override
    public void start(Stage stage) throws Exception {
        GuiView.stage = stage;
        stage.getIcons().add(new Image(Paths.CRANIO_LOGO));
        currentSceneName = DrawingConstants.RESOURCE_LOGIN;
        FXMLLoader fxmlLoader = new FXMLLoader(GuiView.class.getResource("/login.fxml"));

        scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle(Constants.ERIANTYS);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(windowEvent -> {
            // TODO add a dialog that asks if the player wants to exit or not
            windowEvent.consume();
            confirmCloseApp();
        });
        stage.show();
    }
}
