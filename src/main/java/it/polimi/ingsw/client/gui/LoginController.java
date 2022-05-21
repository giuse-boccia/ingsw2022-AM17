package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.GuiView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController implements GuiController {

    @FXML
    private TextField usernameTextField;

    public LoginController() {

    }

    public void onLoginBtnPressed(ActionEvent event) {
        String username = usernameTextField.getText();
        GuiView.getGui().setTmpUsername(username);
        GuiView.getGui().getCurrentObserver().sendLoginParameters(username, null, null);
    }

    @Override
    public void receiveData(Object data) {

    }
}
