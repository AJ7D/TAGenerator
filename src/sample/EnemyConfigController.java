package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    public ComboBox<String> roomSelCbx;

    Game game = GeneratorController.getNewGame();
    public Room oldRoom;

    @FXML
    private void initialize() {
        ArrayList<String> rooms = new ArrayList<>();
        for (Room r : GeneratorController.getNewGame().getGameMap()) {
            rooms.add(r.getName());
        }
        roomSelCbx.getItems().setAll(rooms);
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
        enemy.setCurrentRoom(game.getRoom(roomSelCbx.getValue()));

        tryGiveRoomEnemy(enemy);

        game.updateEnemy(enemy);
        System.out.println(game.getGameEnemies());
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
        enemy = game.getEnemy(str);
        nameEntryTF.setText(enemy.getName());
        healthTF.setText(String.valueOf(enemy.getMaxHp()));
        attackTF.setText(String.valueOf(enemy.getAttack()));
        passiveCheck.setSelected(enemy.getState()==EnemyState.PASSIVE);
        roomSelCbx.getSelectionModel().select(enemy.getCurrentRoom().getName());

        String r = game.findEnemyLoc(enemy).getName();
        if (r != null) {
            roomSelCbx.getSelectionModel().select(r);
        }
        else {
            roomSelCbx.getSelectionModel().selectFirst();
        }
        oldRoom = game.getRoom(r);
    }

    public void tryGiveRoomEnemy(Enemy enemy) {
        Room r = game.getRoom(roomSelCbx.getValue());
        List<Item> inventory = game.getPlayer().getInventory().getContents();
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
