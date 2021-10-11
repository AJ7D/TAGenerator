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

    @FXML
    private void initialize() {

    }

    public void saveRoom() {
        String rName = nameEntryTF.getText();
        String rDesc = roomDescTA.getText();

        if (room == null) {
            room = new Room(rName, rDesc);
        }
        else {
            room.setName(rName);
            room.setDescription(rDesc);
        }
        GeneratorController.getNewGame().updateRoom(room);

        System.out.println(room);
        System.out.println(GeneratorController.getNewGame().getGameMap());
        closeWindow();
    }

    private void closeWindow(){
        Stage stage = (Stage) saveRoomBtn.getScene().getWindow();
        generatorController.callUpdate();
        stage.close();
    }
    
    public void loadRoom(String str) {
        room = GeneratorController.getNewGame().getRoom(str);
        nameEntryTF.setText(room.getName());
        roomDescTA.setText(room.getDescription());
    }

    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }
}
