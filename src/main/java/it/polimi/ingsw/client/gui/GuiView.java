package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.server.game_state.GameState;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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

    public void showToast(String message) {
        if (scene == null) return;
        System.out.println("Toast with message: " + message);
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
