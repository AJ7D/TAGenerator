package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class EngineController {

    public TextField textEntryTa;
    public ScrollPane gameTextSp;
    public TextArea gameTextTa;
    public Button loadGameBtn;

    public GameManager gameManager = new GameManager();

    private EngineState state = EngineState.NOT_LOADED;
    private Game game;
    private Player player;

    @FXML
    private void initialize() {
        textEntryTa.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                String text = textEntryTa.getText();
                gameTextTa.appendText(text + "\n");
                gameTextTa.appendText(engineControl(text) +"\n\n");
                textEntryTa.clear();
            }
        });
    }

    public String engineControl(String input) {
        switch (state) {
            case NOT_LOADED:
                return "NO GAME LOADED.\nPlease load a game to play.";
            case PLAYING:
                return parseInput(input);
            case GAMEOVER:
                return "YOU HAVE LOST.\nIf you wish to play again, please load your last save or" +
                        "start from the beginning.";
            case WIN:
                return "YOU HAVE WON.\nPlease load a new game if you wish to play more.";
        }
        return "ERROR: Unknown state.";
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
                case "location":
                    return player.getBearings();
                default:
                    return "Command not recognised.";
            }
        }
        return "Enter [command] [item]";
    }

    public void loadGame() throws IOException, ClassNotFoundException {
        Stage stage = (Stage) loadGameBtn.getScene().getWindow();
        game = gameManager.loadGameFile(stage);
        player = game.getPlayer();

        if (game != null ) {
            gameTextTa.appendText("Game loaded successfully. Enjoy playing " + game.getTitle() + "!\n");
            state = EngineState.PLAYING;
        }
        else {
            gameTextTa.appendText("Unable to load game. Please check that the file is correct.");
        }
    }
}
