package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class IllegalRoomConnection extends Exception {

    public IllegalRoomConnection(String errorMessage) {
        super(errorMessage);
    }
}
