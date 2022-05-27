package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
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

    private double width, height;
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
        // TODO at least read the following idea
        // IDEA: non mandare un object al Controller, ma setta solo la variabile currentController.
        // Quando arriva un messaggio fai currentController.receiveMessage e a quel punto parsa l'oggetto.
        // In questo modo non si crea una nuova scena ogni volta

        // TODO save in a variable the name of the current scene and do a check: if it is already showed, just pass data,
        // otherwise show it and pass it data
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

    public void sendMessageToController(GameLobby lobby, GameState gameState, List<String> actions, String username) {
        Platform.runLater(() -> currentController.receiveData(lobby, gameState, actions, username));
    }

    public void askCharacterParameters(CharacterName name, boolean requireColor, boolean requireIsland, boolean isSwapCard, boolean moveOneStudentAway) {
        Platform.runLater(() -> currentController.askCharacterParameters(name, requireColor, requireIsland, isSwapCard, moveOneStudentAway));
    }

    public void showToast(String message) {
        if (scene == null) return;
        Platform.runLater(() -> {
            Notifications.create().owner(stage).text(message).hideAfter(Duration.seconds(3)).position(Pos.BOTTOM_CENTER).show();
        });
    }

    public static void showErrorDialog(String message, boolean closeApplication) {
        Alert.AlertType alertType = closeApplication ? Alert.AlertType.ERROR : Alert.AlertType.WARNING;

        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle("Error");
            alert.setHeaderText(message);
            alert.setContentText(null);

            alert.showAndWait();

            if (closeApplication) {
                System.out.println(message);
                System.out.println("The application will now close...");
                System.exit(-1);
            }
        });
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
                GuiView.getGui().getCurrentObserver().sendActionParameters("PLAY_CHARACTER", colorChoiceBox.getValue(), null,
                        null, null, null, ObjectClickListeners.getLastCharacterPlayed(), null, null);
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
                popupStage.close();
            });
        }

        popupStage.setScene(new Scene(anchorPane, width, height));
        popupStage.show();
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
        currentSceneName = "login";
        FXMLLoader fxmlLoader = new FXMLLoader(GuiView.class.getResource("/login.fxml"));

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        width = screen.getWidth();
        height = screen.getHeight();

        scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Eriantys");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(windowEvent -> {
            // TODO add a dialog that asks if the player wants to exit or not
            stage.close();
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }
}
