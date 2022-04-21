package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RoomConfigController {
    public Pane pane;

    public Text title;
    public Text roomIdTxt;
    public Text roomNameTxt;
    public Text roomDescTxt;
    public TextArea roomDescTA;
    public Button saveRoomBtn;
    public TextField nameEntryTF;

    public Room room;

    public GeneratorController generatorController;

    private int MAX_STRING_LENGTH = 50;

    @FXML
    private void initialize() {

    }

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

    private void closeWindow(){
        Stage stage = (Stage) saveRoomBtn.getScene().getWindow();
        generatorController.updateInterfaceDisplay();
        stage.close();
    }
    
    public void loadRoom(Long id) {
        room = GeneratorController.getNewGame().getRoom(id);
        nameEntryTF.setText(room.getName());
        roomDescTA.setText(room.getDescription());
    }

    public void validateInputs() throws InvalidInputException {
        if (nameEntryTF.getText().trim().length() > MAX_STRING_LENGTH ||
                nameEntryTF.getText().trim().length() == 0) {
            throw new InvalidInputException("Please enter a name between 0-50 characters.");
        }

        if (roomDescTA.getText().trim().length() == 0) {
            throw new InvalidInputException("Please enter a description.");
        }
    }

    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }
}
