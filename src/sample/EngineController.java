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

    public static Stage stage;
    private EngineState state = EngineState.NOT_LOADED;
    private Game game;
    private Game oldGame;
    private Player player;
    private int turn = 0;

    @FXML
    private void initialize() {
        //takes user input and appends it to the game's visual event log
        textEntryTa.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) { //user has entered input
                String text = textEntryTa.getText();
                gameTextTa.appendText(text + "\n"); //show user's command in game log
                gameTextTa.appendText(engineControl(text) +"\n"); //show game response in game log
                textEntryTa.clear(); //clear player's command entry
            }
        });
    }

    public String engineControl(String input) {
        //engine handling, returns game log output based on engine state
        switch (state) {
            case NOT_LOADED:
                return "NO GAME LOADED.\nPlease load a game to play.";
            case PLAYING:
                return parseInput(input); //handle user's command
            case GAMEOVER:
                return "YOU HAVE LOST.\nIf you wish to play again, please load your last save or" +
                        "\nstart from the beginning.";
            case WIN:
                return "YOU HAVE WON.\nPlease load a new game if you wish to play more.";
        }
        return "ERROR: Unknown state.";
    }

    public String parseInput(String input) {
        //take user input and determine an action, if legal
        String[] split = input.split(" "); //split input into list of words on " " delimiter

        ArrayList<String> command = new ArrayList<>();
        for (String s : split) {
            if (!stopwords.contains(s) && !s.equals("")) {
                command.add(s); //remove stopwords and whitespace
            }
        }

        String displayText = "";
        if (command.size() == 1) {
            displayText = displayText.concat(executeCommand(command));
        }
        else {
            ArrayList<String> singleCommand = new ArrayList<>();
            for (String s : command) {
                if (s.equals("and")) {
                    //process all of command until now, add to events if valid
                    displayText = displayText.concat(executeCommand(singleCommand) + "\n");
                    singleCommand.clear();
                }
                else {
                    //if word is in object dictionary, check action is applicable
                    singleCommand.add(s);
                }
            }
            displayText = displayText.concat(executeCommand(singleCommand));

        }

        if (game.isWon()) {
            state = EngineState.WIN;
            displayText = displayText.concat("YOU HAVE WON!\n");
        }

        return displayText + "\n";
    }

    public String executeCommand(ArrayList<String> args) {
        turn = player.getTurnCount(); //update number of turns in current game
        if (args.size() > 0) {
            if (grammar.get(args.get(0)) != null) {
                //process actions of player and other npcs
                return grammar.get(args.get(0)).process(player, args) + "\n" + processEnemyResponses();
            }
            else {
                Item i = validateItem(args); //find item for grammar checking
                if (i != null) {
                    if (i.getVerbs().get(args.get(0)) != null) { //grammar is applicable action, return action
                        return i.getVerbs().get(args.get(0)).process(player, args) + "\n" + processEnemyResponses();
                    }
                }
            }
        }
        return "Command " + args.toString() + " not recognised.";
    }

    public void loadGame() throws IOException {
        Stage stage = (Stage) loadGameBtn.getScene().getWindow();
        game = gameManager.loadGameFile(stage); //opens file selection and loads selected file
        oldGame = new Game(game); //store a copy of the original game parameters
        player = game.getPlayer();

        if (game != null ) { //a game has been loaded
            grammar = game.getGrammar();
            gameTextTa.appendText("Game loaded successfully. Enjoy playing " + game.getTitle() + "!\n" +
                    "Enter !help for additional information.");
            state = EngineState.PLAYING;
        }
        else {
            gameTextTa.appendText("Unable to load game. Please check that the file is correct.");
        }
    }

    public void saveGameState() throws IllegalSaveStateException, IOException {
        switch (state) {
            case PLAYING: {
                gameManager.saveGameState(oldGame, game, stage);
                gameTextTa.appendText("Game saved successfully.");
                break;
            }
            case NOT_LOADED: {
                throw new IllegalSaveStateException("No game has been loaded to save.");
            }
            default:
                throw new IllegalSaveStateException("Game cannot be saved after a win or loss.");
        }
    }

    public void loadGameState() throws IOException, FileNotSelectedException, IllegalSaveStateException {
        if (state == EngineState.NOT_LOADED) {
            throw new IllegalSaveStateException("No game loaded, please select a game first.");
        } else {
            game = gameManager.loadGameState(oldGame, stage); //try to load a game state
            player = game.getPlayer();
            gameTextTa.appendText("Save game loaded successfully. You are in " + player.getCurrentRoom().getName() + "\n\n");
        }
    }

    private Item validateItem(ArrayList<String> input) {
        ArrayList<String> args = new ArrayList<>(input);
        args.remove(0); //remove action to parse item only
        if (args.size() == 1) { //single word item, no need for word building
            for (Item i : player.getInteractables()) {
                if (args.get(0).equalsIgnoreCase(i.getName())) //item found
                    return i;
            }
        }
        else {
            for (Item i : player.getInteractables()) {
                for (ArrayList<String> combi : WordBuilderTools.buildComplex(args)) { //get potential word combinations
                    for (String string : combi) {
                        if (string.equalsIgnoreCase(i.getName())) {
                            return i;
                        }
                    }
                }
            }
        }
        return null;
    }

    private String processEnemyResponses() {
        String response = "";
        //process enemy response if enemy present and player has used a turn
        if (!player.getCurrentRoom().getEnemies().isEmpty() && player.getTurnCount() > turn) {
            turn = player.getTurnCount(); //update turn count
            for (Enemy e : player.getCurrentRoom().getEnemies()) {
                response = response.concat(e.processTurn(player) + "\n");
                if (player.getHp() <= 0) { //gameover condition
                    response = response.concat(player.getName() + " has died.");
                    state = EngineState.GAMEOVER;
                    return response;
                }
            }
        }
        return response;
    }
}
