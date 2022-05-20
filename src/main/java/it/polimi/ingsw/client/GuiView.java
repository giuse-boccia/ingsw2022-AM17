package it.polimi.ingsw.client;

import it.polimi.ingsw.client.gui.GuiController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class GuiView extends Application {

    private double width, height;
    private static GUI gui;
    private static Scene scene;
    private static Stage stage;
    private static GuiController currentController;

    public static void main(GUI inputGui) {
        gui = inputGui;
        launch();
    }

    public static GUI getGui() {
        return gui;
    }

    public static void changeScene(String resourceName, boolean fullscreen, Object data) throws IOException {
        // TODO at least read the following idea
        // IDEA: non mandare un object al Controller, ma setta solo la variabile currentController.
        // Quando arriva un messaggio fai currentController.receiveMessage e a quel punto parsa l'oggetto.
        // In questo modo non si crea una nuova scena ogni volta

        FXMLLoader fxmlLoader = new FXMLLoader(GuiView.class.getResource("/" + resourceName + ".fxml"));
        currentController = fxmlLoader.getController();
        scene.setRoot(fxmlLoader.load());
//        scene = new Scene(fxmlLoader.load());
//        stage.setMaximized(fullscreen);
//        stage.setFullScreen(false);
//        stage.setScene(scene);
// stage.show();
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
        FXMLLoader fxmlLoader = new FXMLLoader(GuiView.class.getResource("/login.fxml"));

        Rectangle2D screen = Screen.getPrimary().getBounds();
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
