package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/** Enemy configuration controller for customising an enemy.
 * @see Enemy*/
public class EnemyConfigController {
    /** Pane for holding nodes.*/
    public Pane pane;

    /** The title of the newly generated page.*/
    public Text title;
    /** The ID of the enemy currently open.*/
    public Text enemyIdTxt;
    /** The name of the enemy currently open.*/
    public Text enemyNameTxt;
    /** The button for saving the enemy.*/
    public Button saveEnemyBtn;
    /** The text field for entering enemy's name.*/
    public TextField nameEntryTF;
    /** The text field for entering enemy's health.*/
    public TextField healthTF;
    /** The text field for entering enemy's attack.*/
    public TextField attackTF;
    /** The checkbox for selecting enemy's state.*/
    public CheckBox passiveCheck;

    /** The enemy being created or edited.*/
    public Enemy enemy;

    /** Reference to the generator controller for updating display.*/
    public GeneratorController generatorController;
    /** The combo box for selecting the enemy's current room.*/
    public ComboBox<Room> roomSelCbx;

    /** The game file that holds this enemy.*/
    Game game = GeneratorController.getNewGame();
    /** Stores the old room of the enemy in case it is updated.*/
    public Room oldRoom;

    /** The maximum length for enemy's name.*/
    private final int MAX_STRING_LENGTH = 50;

    /** Initialises the window, configuring the interface.*/
    @FXML
    private void initialize() {
        UITools.configureComboboxRoom(roomSelCbx); //set roomselcbx to contain room object references
        roomSelCbx.getItems().setAll(GeneratorController.getNewGame().getGameMap()); //all valid rooms in gamemap
        roomSelCbx.getSelectionModel().selectFirst(); //select first room in gamemap by default
    }

    /** Saves the enemy to the current game.*/
    public void saveEnemy()  {
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

            tryGiveRoomEnemy(enemy); //place enemy into indicated room

            game.updateEnemy(enemy);
            closeWindow();
        }
        catch (InvalidInputException e) { //prompt user to correct any invalid input
            System.out.println(e.toString()); //for debugging if needed
        }
    }

    /** Deletes the selected enemy from the game.*/
    public void deleteEnemy() { //delete enemy of current window and close window
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
    public void tryGiveRoomEnemy(Enemy enemy) {
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
    public EnemyState determineEnemyState() {
        //quick conversion of checkbox selection -> EnemyState
        if (passiveCheck.isSelected())
            return EnemyState.PASSIVE;
        return EnemyState.AGGRESSIVE;
    }

    /** Validates all user input before saving the enemy. Throws an exception if any field is invalid.
     * @throws InvalidInputException*/
    public void validateInputs() throws InvalidInputException {
        if (nameEntryTF.getText().trim().length() > MAX_STRING_LENGTH ||
            nameEntryTF.getText().trim().length() == 0) {
            throw new InvalidInputException("Please enter a name between 0-50 characters.");
        }

        try {
            Integer.parseInt(healthTF.getText());
        }
        catch (NumberFormatException e) {
            throw new InvalidInputException("Please enter a valid number for HP (e.g. 20).");
        }

        try {
            Integer.parseInt(attackTF.getText());
        }
        catch (NumberFormatException e) {
            throw new InvalidInputException("Please enter a valid number for attack damage (e.g. 5).");
        }
    }

    /** Sets the generator controller window this window is associated with.
     * @param gc The generator controller reference to be set.*/
    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }
}
