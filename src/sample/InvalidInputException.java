package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class InvalidInputException extends Exception {

    public InvalidInputException(String errorMessage) {
        super(errorMessage);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Failed to save input");
        alert.setContentText(errorMessage);

        Optional<ButtonType> option = alert.showAndWait();
    }
}
