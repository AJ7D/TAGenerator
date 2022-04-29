package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/** Enemy configuration controller for customising a room.
 * @see Room*/
public class RoomConfigController {
    /** Pane for holding nodes.*/
    public Pane pane;

    /** The title of the newly generated page.*/
    public Text title;
    /** The ID of the enemy currently open.*/
    public Text roomIdTxt;
    /** The text indicating room name entry.*/
    public Text roomNameTxt;
    /** The text indicating room description entry.*/
    public Text roomDescTxt;
    /** The text area for description entry.*/
    public TextArea roomDescTA;
    /** The button for saving the current room.*/
    public Button saveRoomBtn;
    /** The text area for name entry.*/
    public TextField nameEntryTF;

    /** Reference of the room being edited.*/
    public Room room;

    /** Reference of the generator controller for updating interface.*/
    public GeneratorController generatorController;

    /** Maximum number of characters for room name.*/
    private int MAX_STRING_LENGTH = 50;

    @FXML
    private void initialize() {

    }

    /** Saves the room to the current game if input passes validation.*/
    public void saveRoom() {
        String rName = nameEntryTF.getText();
        String rDesc = roomDescTA.getText();

        try {
            validateInputs();
            if (room == null) {
                room = new Room(rName, rDesc);
            } else {
                room.setName(rName);
                room.setDescription(rDesc);
            }
            GeneratorController.getNewGame().updateRoom(room);

            closeWindow();
        } catch (InvalidInputException e) {
            System.out.println(e.toString()); //for debugging if needed
        }
    }

    /** Closes the current room configuration window and returns to the generator.*/
    private void closeWindow(){
        Stage stage = (Stage) saveRoomBtn.getScene().getWindow();
        generatorController.updateInterfaceDisplay();
        stage.close();
    }

    /** Loads the given room details into the configuration window.
     * @param id The unique identifier of the room used for lookup.*/
    public void loadRoom(Long id) {
        room = GeneratorController.getNewGame().getRoom(id);
        nameEntryTF.setText(room.getName());
        roomDescTA.setText(room.getDescription());
    }

    /** Validates all user input before saving the enemy. Throws an exception if any field is invalid.
     * @throws InvalidInputException if any validation checks fail.*/
    public void validateInputs() throws InvalidInputException {
        if (nameEntryTF.getText().trim().length() > MAX_STRING_LENGTH ||
                nameEntryTF.getText().trim().length() == 0) {
            throw new InvalidInputException("Please enter a name between 0-50 characters.");
        }

        if (roomDescTA.getText().trim().length() == 0) {
            throw new InvalidInputException("Please enter a description.");
        }
    }

    /** Sets the generator controller window this window is associated with.
     * @param gc The generator controller reference to be set.*/
    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }
}
