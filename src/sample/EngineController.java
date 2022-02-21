package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EngineController {

    public TextField textEntryTa;
    public ScrollPane gameTextSp;
    public TextArea gameTextTa;
    public Button loadGameBtn;

    public GameManager gameManager = new GameManager();
    private final List<String> stopwords = Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now");

    private HashMap<String, Action> grammar = new HashMap<>();

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

        ArrayList<String> command = new ArrayList<>();
        for (String s : split) {
            if (!stopwords.contains(s) && !s.equals("")) {
                command.add(s);
            }
        }

        System.out.println(command);

        if (command.size() == 1) {
            System.out.println("Doing single word command.");
            return executeCommand(command);
        }
        else {
            String displayText = "";
            ArrayList<String> singleCommand = new ArrayList<>();
            for (String s : command) {
                if (s.equals("and")) {
                    //process all of command until now, add to a list of events if valid
                    displayText = displayText.concat(executeCommand(singleCommand) + "\n");
                    singleCommand.clear();
                    System.out.println("AND detected. Separating commands.");
                }
                else {
                    //if word is in object dictionary, check action is applicable
                    singleCommand.add(s);
                    System.out.println("Doing multiple word command.");
                }
            }
            displayText = displayText.concat(executeCommand(singleCommand) + "\n");
            return displayText;
        }
    }

    public String executeCommand(ArrayList<String> args) {
        if (args.size() > 0) {
            if (grammar.get(args.get(0)) != null) {
                return grammar.get(args.get(0)).process(player, args);
            }
        }
        return "Command " + args.toString() + " not recognised.";
    }

    public void loadGame() throws IOException, ClassNotFoundException {
        Stage stage = (Stage) loadGameBtn.getScene().getWindow();
        game = gameManager.loadGameFile(stage);
        player = game.getPlayer();

        if (game != null ) {
            grammar = game.getGrammar();
            gameTextTa.appendText("Game loaded successfully. Enjoy playing " + game.getTitle() + "!\n" +
                    "Enter !help for additional information.");
            state = EngineState.PLAYING;
        }
        else {
            gameTextTa.appendText("Unable to load game. Please check that the file is correct.");
        }
    }

}
