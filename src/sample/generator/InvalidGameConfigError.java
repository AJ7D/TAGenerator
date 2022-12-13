package sample.generator;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/** Exception when a game has been configured to an unplayable state.*/
public class InvalidGameConfigError extends Exception {

    /** Displays a visual error message to the player.
     * @param errorMessage The error message to be displayed in the error window.*/
    public InvalidGameConfigError(String errorMessage) {
        super(errorMessage);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invalid Game Parameters");
        alert.setContentText(errorMessage);

        alert.showAndWait();
    }
}
