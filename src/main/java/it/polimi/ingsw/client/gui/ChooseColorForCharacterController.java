package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
import it.polimi.ingsw.model.game_objects.Color;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class ChooseColorForCharacterController {
    private final ObservableList<Color> colors = FXCollections.observableArrayList(Color.values());
    @FXML
    private ChoiceBox<Color> characterColorPicker;
    @FXML
    private Button btnConfirmColor;

    @FXML
    public void initialize() {
        characterColorPicker.setValue(colors.get(0));
        characterColorPicker.setItems(colors);

        btnConfirmColor.setOnMouseClicked(event -> {
            sendColor();
            Stage currentStage = (Stage) btnConfirmColor.getScene().getWindow();
            currentStage.close();
        });
    }

    private void sendColor() {
        GuiView.getGui().getCurrentObserver().sendActionParameters("PLAY_CHARACTER", characterColorPicker.getValue(), null,
                null, null, null, ObjectClickListeners.getLastCharacterPlayed(), null, null);
    }
}
