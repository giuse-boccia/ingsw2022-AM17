package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.utils.DrawingComponents;
import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.server.game_state.GameState;
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
                gui.gracefulTermination("Connection to server lost");
            }
            stage.setResizable(fullscreen);
            stage.setFullScreen(false);
            stage.setScene(scene);
            currentController = fxmlLoader.getController();
        });
    }

    public void startGameScene() {
        Platform.runLater(() -> {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            double screenWidth = bounds.getWidth();
            double screenHeight = bounds.getHeight();
            AnchorPane root = new AnchorPane();
            root.setId(DrawingConstants.ID_ROOT_GAME);
            scene = new Scene(root, screenWidth, screenHeight);
            scene.getStylesheets().add("/css/style.css");

            ActionController newController = new ActionController();
            newController.setRoot(root);
            newController.initialize();
            currentController = newController;

            stage.setResizable(false);
            stage.setMaximized(true);
            stage.setScene(scene);
        });
    }

    public void sendMessageToController(GameLobby lobby, GameState gameState, List<String> actions, String username) {
        Platform.runLater(() -> currentController.receiveData(lobby, gameState, actions, username));
    }

    public void askCharacterParameters(CharacterName name, GuiCharacterType characterType) {
        Platform.runLater(() -> currentController.askCharacterParameters(name, characterType));
    }

    public static void showPopupForColorOrBound(int bound) {
        Stage popupStage = getNewUndecoratedStage();
        double width = DrawingConstants.CHARACTER_POPUP_WIDTH;
        double height = DrawingConstants.CHARACTER_POPUP_HEIGHT;

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(width, height);

        String titleText = bound == -1 ? Messages.CHOOSE_COLOR_CHARACTER : Messages.CHOOSE_BOUND_CHARACTER;
        Text title = new Text(titleText);
        title.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.SUBTITLE_FONT_SIZE));
        StackPane titleStackPane = new StackPane(title);
        titleStackPane.setPrefWidth(width);
        titleStackPane.setLayoutY(height * DrawingConstants.CHARACTER_POPUP_TITLE_OFFSET_Y);
        anchorPane.getChildren().add(titleStackPane);

        Button confirmBtn = new Button("Confirm");
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

    public void showErrorDialog(String message, boolean closeApplication) {
        // If there's a connection error the app should be closed without showing the alert - also to avoid
        // showing the login screen when the app can't start
        if (Objects.equals(message, Messages.USAGE) || Objects.equals(message, Messages.JSON_NOT_FOUND)
                || Objects.equals(message, Messages.INVALID_SERVER_PORT) || Objects.equals(message, Messages.PORT_NOT_AVAILABLE)
                || Objects.equals(message, Messages.CANNOT_CONNECT_TO_SERVER)) {
            closeAppWithErrorMessage(message);
            return;
        }
        Alert.AlertType alertType = closeApplication ? Alert.AlertType.ERROR : Alert.AlertType.WARNING;

        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle("Error");
            alert.setHeaderText(message);
            alert.setContentText(null);
            alert.initOwner(stage);

            alert.showAndWait();

            if (closeApplication) {
                closeAppWithErrorMessage(message);
            }
        });
    }

    private void closeAppWithErrorMessage(String message) {
        System.out.println(message);
        System.out.println(Messages.APPLICATION_CLOSING);
        System.exit(-1);
    }

    private void confirmCloseApp() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(Messages.CLOSE_GAME_TITLE);
            alert.setHeaderText(Messages.CONFIRM_CLOSE_GAME);

            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                stage.close();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void showToast(String message) {
        if (scene == null) return;
        Platform.runLater(() ->
                Notifications.create().owner(stage).text(message)
                        .hideAfter(Duration.seconds(Constants.TOAST_DURATION_SECONDS))
                        .position(Pos.BOTTOM_CENTER)
                        .show()
        );
    }

    public void endGame(String message) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(Messages.END_GAME_TITLE);

            Text endGameText = new Text(message);
            endGameText.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.SUBTITLE_FONT_SIZE));
            Button closeAppBtn = new Button();
            closeAppBtn.setText(Messages.END_GAME_BUTTON_TEXT);
            closeAppBtn.setStyle("-fx-font-family: Algerian; -fx-font-size: 16");
            closeAppBtn.setOnMouseClicked(event -> {
                System.out.println(Messages.GAME_ENDED_MESSAGE);
                System.exit(0);
            });
            VBox vBox = new VBox(endGameText, closeAppBtn);
            vBox.setAlignment(Pos.CENTER);

            stage.setScene(new Scene(vBox, DrawingConstants.END_GAME_POPUP_WIDTH, DrawingConstants.END_GAME_POPUP_HEIGHT));
            stage.show();
        });
    }

    private static Stage getNewUndecoratedStage() {
        Stage newStage = new Stage();
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(scene.getWindow());
        newStage.initStyle(StageStyle.UNDECORATED);
        return newStage;
    }


    @Override
    public void start(Stage stage) throws Exception {
        GuiView.stage = stage;
        stage.getIcons().add(new Image("/gameboard/cranio_logo.png"));
        currentSceneName = "login";
        FXMLLoader fxmlLoader = new FXMLLoader(GuiView.class.getResource("/login.fxml"));

        scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Eriantys");
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
