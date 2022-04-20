package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ExitConfigController {
    public Pane pane;

    public Text title;
    public Text roomDirTxt;
    public CheckBox isLockedChx;
    public Button saveExitBtn;
    public Button newReqBtn;

    public Room room;
    public Direction direction;

    public GeneratorController generatorController;
    public ComboBox<Room> roomSelCbx;

    Game game = GeneratorController.getNewGame();

    @FXML
    private void initialize() {
        UITools uit = new UITools(); //methods for clean configuring javafx nodes
        uit.configureComboboxRoom(roomSelCbx); //set roomselcbx to contain room references
        roomSelCbx.getSelectionModel().selectFirst(); //get first room in room list
        isLockedChx.setOnAction(eventHandler);
    }

    public void saveExit() throws IllegalRoomConnection {
        if (roomSelCbx.getValue() != null) { //connect rooms if valid room selected
            Room toConnect = roomSelCbx.getValue();
            game.connectRooms(room, direction, toConnect);
        }
        room.setIsLocked(isLockedChx.isSelected(), direction); //update room to show new connection info

        generatorController.newRoomDisplay(String.valueOf(room.getId())); //update display on generator to reflect change

        closeWindow();
    }

    private void closeWindow(){
        //close this exit configuration window and update generator interface to reflect changes
        Stage stage = (Stage) saveExitBtn.getScene().getWindow();
        generatorController.updateInterfaceDisplay();
        stage.close();
    }
    
    public void loadRoom(Room rm, Direction dir) {
        //load information about selected room for display
        room = rm;
        direction = dir;
        roomDirTxt.setText("To the " + dir + " of " + room.getName() + " is...");

        ArrayList<Room> rooms = new ArrayList<>(GeneratorController.getNewGame().getGameMap());
        rooms.remove(room); //remove this room, cannot connect room to itself
        roomSelCbx.getItems().setAll(rooms);

        Room connected = room.getExit(dir);
        //check if room already has an exit assigned in this direction, otherwise select first room in list
        if (connected != null) {
            roomSelCbx.getSelectionModel().select(connected);
        }
        else {
            roomSelCbx.getSelectionModel().selectFirst();
        }

        isLockedChx.setSelected(room.isDirectionBlocked(dir)); //reflect locked status in checkbox
        newReqBtn.setDisable(!isLockedChx.isSelected()); //disable requirements button if islocked = false
    }

    EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (event.getSource().equals(isLockedChx)) {
                if (isLockedChx.isSelected()) {
                    newReqBtn.setDisable(false);
                }
                else {
                    newReqBtn.setDisable(true);
                }
            }
        }
    };

    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }
}
