package sample.generator;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.game.Enemy;
import sample.game.EnemyState;
import sample.game.Game;
import sample.game.Room;
import sample.tools.UITools;
import sample.tools.ValidationTools;

/** Enemy configuration controller for customising an enemy.
 * @see Enemy*/
public class EnemyConfigController {
    /** Pane for holding nodes.*/
    @FXML
    private Pane pane;

    /** The title of the newly generated page.*/
    @FXML
    private Text title;
    /** The text indicating enemy's name entry.*/
    @FXML
    private Text enemyNameTxt;
    /** The button for saving the enemy.*/
    @FXML
    private Button saveEnemyBtn;
    /** The text field for entering enemy's name.*/
    @FXML
    private TextField nameEntryTF;
    /** The text field for entering enemy's health.*/
    @FXML
    private TextField healthTF;
    /** The text field for entering enemy's attack.*/
    @FXML
    private TextField attackTF;
    /** The checkbox for selecting enemy's state.*/
    @FXML
    private CheckBox passiveCheck;

    /** The enemy being created or edited.*/
    private Enemy enemy;

    /** Reference to the generator controller for updating display.*/
    public GeneratorController generatorController;
    /** The combo box for selecting the enemy's current room.*/
    @FXML
    private ComboBox<Room> roomSelCbx;

    /** The game file that holds this enemy.*/
    private Game game = GeneratorController.getNewGame();
    /** Stores the old room of the enemy in case it is updated.*/
    private Room oldRoom;

    /** The maximum length for enemy's name.*/
    private final int MAX_STRING_LENGTH = 50;

    /** Initialises the window, configuring the interface.*/
    @FXML
    private void initialize() {
        UITools.configureComboboxRoom(roomSelCbx); //set roomselcbx to contain room object references
        roomSelCbx.getItems().setAll(GeneratorController.getNewGame().getGameMap()); //all valid rooms in gamemap
        roomSelCbx.getSelectionModel().selectFirst(); //select first room in gamemap by default
    }

    /** Saves the enemy to the current game if input passes validation.*/
    @FXML
    private void saveEnemy()  {
        if (enemy == null) {
            //create new enemy if not updating an existing one
            enemy = new Enemy();
        }

        try {
            validateInputs(); //validates all user input to ensure it is safe for storage
            //set enemy attributes
            enemy.setName(nameEntryTF.getText().trim());
            enemy.initialiseHp(Integer.parseInt(healthTF.getText()));
            enemy.setAttack(Integer.parseInt(attackTF.getText()));
            enemy.setState(determineEnemyState());
            enemy.setCurrentRoom(roomSelCbx.getValue());

            tryPlaceEnemyInSetRoom(enemy); //place enemy into indicated room

            game.updateEnemy(enemy);
            closeWindow();
        }
        catch (InvalidInputException e) { //prompt user to correct any invalid input
            System.out.println(e.toString()); //for debugging if needed
        }
    }

    /** Deletes the selected enemy from the game.*/
    @FXML
    private void deleteEnemy() { //delete enemy of current window and close window
        game.deleteEnemy(enemy);
        closeWindow();
    }

    /** Closes the enemy configuration window currently open.*/
    private void closeWindow(){
        //closes this window instance and updates generator window to show any changes
        Stage stage = (Stage) saveEnemyBtn.getScene().getWindow();
        generatorController.updateInterfaceDisplay();
        stage.close();
    }

    /** Loads an existing enemy's information into the enemy configuration window.
     * @param str The ID of the enemy to be looked up, passed as a String through button ID.*/
    public void loadEnemy(String str) { //finds and loads an enemy based on given string id
        enemy = game.getEnemy(Long.parseLong(str));
        nameEntryTF.setText(enemy.getName());
        healthTF.setText(String.valueOf(enemy.getMaxHp()));
        attackTF.setText(String.valueOf(enemy.getAttack()));
        passiveCheck.setSelected(enemy.getState()==EnemyState.PASSIVE);
        roomSelCbx.getSelectionModel().select(enemy.getCurrentRoom());

        Room r = enemy.getCurrentRoom();
        if (r != null) {
            roomSelCbx.getSelectionModel().select(r); //set selected room to enemy's room
        }
        else {
            //if enemy room is not set, get first room in list of rooms
            roomSelCbx.getSelectionModel().selectFirst();
        }
        oldRoom = r; //record old room so we can easily remove the enemy from it if changed
    }

    /** Tries to place the enemy into the selected room when saving.
     * @param enemy The enemy to attempt placing into the current room*/
    private void tryPlaceEnemyInSetRoom(Enemy enemy) {
        //gets selected room from combobox and adds enemy to it
        Room r = roomSelCbx.getValue();
        if (!r.containsEnemy(enemy)) {
            if (oldRoom != null) {
                oldRoom.deleteEnemy(enemy);
            }

            r.addEnemy(enemy);
        }
    }

    /** Determines the enemy state to be saved by checking if check box is selected.
     * @return EnemyState The state of the enemy, restricted to AGGRESSIVE or PASSIVE.*/
    private EnemyState determineEnemyState() {
        //quick conversion of checkbox selection -> EnemyState
        if (passiveCheck.isSelected())
            return EnemyState.PASSIVE;
        return EnemyState.AGGRESSIVE;
    }

    /** Validates all user input before saving the enemy. Throws an exception if any field is invalid.
     * @throws InvalidInputException if any validation checks fail.*/
    private void validateInputs() throws InvalidInputException {
        if (nameEntryTF.getText().trim().length() > MAX_STRING_LENGTH ||
            nameEntryTF.getText().trim().length() == 0) {
            throw new InvalidInputException("Please enter a name between 0-50 characters.");
        }

        ValidationTools.CheckNumericAboveZero("HP", healthTF.getText());
        ValidationTools.CheckNumericAboveZero("attack damage", attackTF.getText());
    }

    /** Sets the generator controller window this window is associated with.
     * @param gc The generator controller reference to be set.*/
    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }
}
