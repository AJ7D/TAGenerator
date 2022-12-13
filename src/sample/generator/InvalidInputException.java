package sample.generator;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/** Exception when the user has failed to input entity parameters that are legal.*/
public class InvalidInputException extends Exception {

    /** Displays a visual error message to the player.
     * @param errorMessage The error message to be displayed in the error window.*/
    public InvalidInputException(String errorMessage) {
        super(errorMessage);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Failed to save input");
        alert.setContentText(errorMessage);

        Optional<ButtonType> option = alert.showAndWait();
    }
}
