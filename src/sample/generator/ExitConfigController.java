package sample.generator;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.game.Direction;
import sample.game.Game;
import sample.game.Room;
import sample.tools.UITools;

import java.util.ArrayList;

/** Exit configuration controller for attaching rooms to each other.
 * @see Room */
public class ExitConfigController {
    /** The pane for holding nodes.*/
    @FXML
    private Pane pane;

    /** The title of the new window.*/
    @FXML
    private Text title;
    /** Text for displaying the room and direction in which this room is being connected.*/
    @FXML
    private Text roomDirTxt;
    /** Button for saving the exit.*/
    @FXML
    private Button saveExitBtn;
    /** Button for adding a new requirement to unlock room traversal.*/
    @FXML
    private Button newReqBtn;

    /** The room to be connected to.*/
    private Room room;
    /** The direction from the room to connect from.*/
    private Direction direction;

    /** Stores reference to main generator controller window for updating interface.*/
    public GeneratorController generatorController;
    /** Combo box for selecting the other room to be attached.*/
    @FXML
    private ComboBox<Room> roomSelCbx;

    /** Reference of the game being edited in the generator.*/
    Game game = GeneratorController.getNewGame();

    /**Initialises the window, configuring the appearance.*/
    @FXML
    private void initialize() {
        UITools.configureComboboxRoom(roomSelCbx); //set roomselcbx to contain room references
        roomSelCbx.getSelectionModel().selectFirst(); //get first room in room list
    }

    /** Called when the save button is clicked by the user. Saves the room configuration.
     * @throws IllegalRoomConnection if trying to connect a room that cannot be connected.*/
    @FXML
    private void saveExit() throws IllegalRoomConnection {
        if (roomSelCbx.getValue() != null) { //connect rooms if valid room selected
            Room toConnect = roomSelCbx.getValue();
            game.connectRooms(room, direction, toConnect);
        }

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

    }

    /** Gets generator controller reference for updating interface
     * @param gc The generator controller to be passed.*/
    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }
}
