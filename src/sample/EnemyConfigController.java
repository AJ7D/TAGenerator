package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

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

    @FXML
    private void initialize() {
        UITools uit = new UITools();
        uit.configureComboboxRoom(roomSelCbx);
        roomSelCbx.getItems().setAll(GeneratorController.getNewGame().getGameMap());
        roomSelCbx.getSelectionModel().selectFirst();
    }

    public void saveEnemy() {
        if (enemy == null) {
            enemy = new Enemy();
        }
        enemy.setName(nameEntryTF.getText());
        enemy.initialiseHp(Integer.parseInt(healthTF.getText()));
        enemy.setAttack(Integer.parseInt(attackTF.getText()));
        enemy.setState(determineEnemyState());
        enemy.setCurrentRoom(roomSelCbx.getValue());

        tryGiveRoomEnemy(enemy);

        game.updateEnemy(enemy);
        System.out.println(game.getGameEnemies().toString());
        System.out.println(enemy);
        System.out.println(enemy.getInventory().getContents());
        closeWindow();
    }

    public void deleteEnemy() {
        game.deleteEnemy(enemy);
        closeWindow();
    }

    private void closeWindow(){
        Stage stage = (Stage) saveEnemyBtn.getScene().getWindow();
        generatorController.callUpdate();
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
            roomSelCbx.getSelectionModel().selectFirst();
        }
        oldRoom = r;
    }

    public void tryGiveRoomEnemy(Enemy enemy) {
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

    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }
}
