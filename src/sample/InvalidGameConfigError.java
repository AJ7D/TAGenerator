package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class InvalidGameConfigError extends Exception {

    public InvalidGameConfigError(String errorMessage) {
        super(errorMessage);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invalid Game Parameters");
        alert.setContentText(errorMessage);
    }
}
