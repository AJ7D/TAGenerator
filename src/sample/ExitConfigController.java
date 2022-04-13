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
        UITools uit = new UITools();
        uit.configureComboboxRoom(roomSelCbx);
        roomSelCbx.getSelectionModel().selectFirst();
        isLockedChx.setOnAction(eventHandler);
    }

    public void saveExit() {
        if (roomSelCbx.getValue() != null) {
            Room toConnect = roomSelCbx.getValue();
            game.connectRooms(room, direction, toConnect);
            System.out.println(toConnect.getExits());
        }
        room.setIsLocked(isLockedChx.isSelected(), direction);

        generatorController.newRoomDisplay(String.valueOf(room.getId()));

        System.out.println(room.getExits());
        closeWindow();
    }

    private void closeWindow(){
        Stage stage = (Stage) saveExitBtn.getScene().getWindow();
        generatorController.callUpdate();
        stage.close();
    }
    
    public void loadRoom(Room rm, Direction dir) {
        room = rm;
        direction = dir;
        roomDirTxt.setText("To the " + dir + " of " + room.getName() + " is...");

        ArrayList<Room> rooms = new ArrayList<>();
        for (Room r : GeneratorController.getNewGame().getGameMap()) {
            if (!r.compareRoom(room)) {
                rooms.add(r);
            }
        }
        roomSelCbx.getItems().setAll(rooms);

        Room connected = room.getExit(dir);
        if (connected != null) {
            roomSelCbx.getSelectionModel().select(connected);
        }
        else {
            roomSelCbx.getSelectionModel().selectFirst();
        }

        isLockedChx.setSelected(room.isDirectionBlocked(dir));
        newReqBtn.setDisable(!isLockedChx.isSelected());
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
