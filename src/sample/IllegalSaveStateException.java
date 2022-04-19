package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class IllegalSaveStateException extends Exception {

    public IllegalSaveStateException(String errorMessage) {
        super(errorMessage);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game cannot be saved");
        alert.setContentText(errorMessage);

        Optional<ButtonType> option = alert.showAndWait();
    }
}
