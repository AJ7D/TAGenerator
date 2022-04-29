package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

/** Exit configuration controller for attaching rooms to each other.
 * @see Room*/
public class ExitConfigController {
    /** The pane for holding nodes.*/
    public Pane pane;

    /** The title of the new window.*/
    public Text title;
    /** Text for displaying the room and direction in which this room is being connected.*/
    public Text roomDirTxt;
    /** Check box for selecting if the room can be travelled to or is locked.*/
    public CheckBox isLockedChx;
    /** Button for saving the exit.*/
    public Button saveExitBtn;
    /** Button for adding a new requirement to unlock room traversal.*/
    public Button newReqBtn;

    /** The room to be connected to.*/
    public Room room;
    /** The direction from the room to connect from.*/
    public Direction direction;

    /** Stores reference to main generator controller window for updating interface.*/
    public GeneratorController generatorController;
    /** Combo box for selecting the other room to be attached.*/
    public ComboBox<Room> roomSelCbx;

    /** Reference of the game being edited in the generator.*/
    Game game = GeneratorController.getNewGame();

    /**Initialises the window, configuring the appearance.*/
    @FXML
    private void initialize() {
        UITools.configureComboboxRoom(roomSelCbx); //set roomselcbx to contain room references
        roomSelCbx.getSelectionModel().selectFirst(); //get first room in room list
        isLockedChx.setOnAction(eventHandler);
    }

    /** Called when the save button is clicked by the user. Saves the room configuration.
     * @throws IllegalRoomConnection if trying to connect a room that cannot be connected.*/
    public void saveExit() throws IllegalRoomConnection {
        if (roomSelCbx.getValue() != null) { //connect rooms if valid room selected
            Room toConnect = roomSelCbx.getValue();
            game.connectRooms(room, direction, toConnect);
        }
        room.setIsLocked(isLockedChx.isSelected(), direction); //update room to show new connection info

        generatorController.newRoomDisplay(String.valueOf(room.getId())); //update display on generator to reflect change

        closeWindow();
    }

    /** Closes the current window.*/
    private void closeWindow(){
        //close this exit configuration window and update generator interface to reflect changes
        Stage stage = (Stage) saveExitBtn.getScene().getWindow();
        generatorController.updateInterfaceDisplay();
        stage.close();
    }

    /** Loads a given room's data into the configuration window.
     * @param rm The room to load into configuration.
     * @param dir The room direction being edited.*/
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

    /** Event handler that disables the new requirement button if the passage is not locked.*/
    EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (event.getSource().equals(isLockedChx)) {
                newReqBtn.setDisable(!isLockedChx.isSelected());
            }
        }
    };

    /** Gets generator controller reference for updating interface.*/
    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }
}
