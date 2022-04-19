package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class EnemyConfigController {
    public Pane pane;

    public Text title;
    public Text enemyIdTxt;
    public Text enemyNameTxt;
    public Button saveEnemyBtn;
    public TextField nameEntryTF;
    public TextField healthTF;
    public TextField attackTF;
    public CheckBox passiveCheck;

    public Enemy enemy;

    public GeneratorController generatorController;
    public ComboBox<Room> roomSelCbx;

    Game game = GeneratorController.getNewGame();
    public Room oldRoom;

    private final int MAX_STRING_LENGTH = 50;

    @FXML
    private void initialize() {
        UITools uit = new UITools();
        uit.configureComboboxRoom(roomSelCbx);
        roomSelCbx.getItems().setAll(GeneratorController.getNewGame().getGameMap());
        roomSelCbx.getSelectionModel().selectFirst();
    }

    public void saveEnemy() throws InvalidInputException {
        if (enemy == null) {
            //create new enemy if not updating an existing one
            enemy = new Enemy();
        }

        try {
            validateInputs();
            //set enemy attributes
            enemy.setName(nameEntryTF.getText().trim());
            enemy.initialiseHp(Integer.parseInt(healthTF.getText()));
            enemy.setAttack(Integer.parseInt(attackTF.getText()));
            enemy.setState(determineEnemyState());
            enemy.setCurrentRoom(roomSelCbx.getValue());

            tryGiveRoomEnemy(enemy); //place enemy into indicated room

            game.updateEnemy(enemy);
            System.out.println(game.getGameEnemies().toString());
            System.out.println(enemy);
            System.out.println(enemy.getInventory().getContents());
            closeWindow();
        }
        catch (InvalidInputException e) {
            System.out.println(e.toString());
        }
    }

    public void deleteEnemy() {
        game.deleteEnemy(enemy);
        closeWindow();
    }

    private void closeWindow(){
        Stage stage = (Stage) saveEnemyBtn.getScene().getWindow();
        generatorController.updateInterfaceParameters();
        stage.close();
    }

    public void loadEnemy(String str) {
        enemy = game.getEnemy(Long.parseLong(str));
        nameEntryTF.setText(enemy.getName());
        healthTF.setText(String.valueOf(enemy.getMaxHp()));
        attackTF.setText(String.valueOf(enemy.getAttack()));
        passiveCheck.setSelected(enemy.getState()==EnemyState.PASSIVE);
        roomSelCbx.getSelectionModel().select(enemy.getCurrentRoom());

        Room r = enemy.getCurrentRoom();
        if (r != null) {
            roomSelCbx.getSelectionModel().select(r);
        }
        else {
            //if enemy room is not set, get first room in list of rooms
            roomSelCbx.getSelectionModel().selectFirst();
        }
        oldRoom = r; //record old room so we can remove the enemy from it if changed
    }

    public void tryGiveRoomEnemy(Enemy enemy) {
        //gets selected room from combobox and adds enemy to it
        Room r = roomSelCbx.getValue();
        if (!r.containsEnemy(enemy)) {
            if (oldRoom != null) {
                oldRoom.deleteEnemy(enemy);
            }

            r.addEnemy(enemy);
            System.out.println(r);
        }
    }

    public EnemyState determineEnemyState() {
        if (passiveCheck.isSelected())
            return EnemyState.PASSIVE;
        return EnemyState.AGGRESSIVE;
    }

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

    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }
}
