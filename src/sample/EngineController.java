package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class EngineController {

    public TextField textEntryTa;
    public VBox gameTextVbox;
    public ScrollPane gameTextSp;
    public TextArea gameTextTa;

    Game game = GeneratorController.getNewGame();
    Player player = game.getPlayer();

    @FXML
    private void initialize() {
        player.setCurrentRoom(game.getGameMap().get(0));
        textEntryTa.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {
                    String text = textEntryTa.getText();
                    gameTextTa.appendText(text + "\n");
                    gameTextTa.appendText(parseInput(text) +"\n\n");
                    textEntryTa.clear();
                }
            }
        });
    }

    public String parseInput(String input) {
        String[] split = input.split(" ");

        if (split.length == 2) {
            switch (split[0]) {
                case "take":
                    return player.acquire(split[1]);
                case "drop":
                    return player.drop(split[1]);
                case "look":
                    return player.checkSurroundings();
                case "self":
                    return player.viewSelf();
                case "north":
                    return player.travel(Direction.NORTH);
                case "east":
                    return player.travel(Direction.EAST);
                case "west":
                    return player.travel(Direction.WEST);
                case "south":
                    return player.travel(Direction.SOUTH);
                case "view":
                    return player.viewItem(split[1]);
                case "inventory":
                    return player.checkInventory();
                default:
                    return "Command not recognised.";
            }
        }
        return "Enter [command] [item]";
    }
}
