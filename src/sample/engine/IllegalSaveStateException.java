package sample.engine;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/** Exception when a game save file is not compatible with a loaded game configuration.*/
public class IllegalSaveStateException extends Exception {

    /** Displays a visual error message to the player.
     * @param errorMessage The error message to be displayed in the error window.*/
    public IllegalSaveStateException(String errorMessage) {
        super(errorMessage);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game cannot be saved");
        alert.setContentText(errorMessage);

        Optional<ButtonType> option = alert.showAndWait();
    }
}
