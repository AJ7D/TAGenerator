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

/** Class for the engine controller.*/
public class EngineController {
    /** Area for the user to input text commands.*/
    public TextField textEntryTa;
    /** Scroll pane for viewing all input/output.*/
    public ScrollPane gameTextSp;
    /** Text area for holding all user input and engine output.*/
    public TextArea gameTextTa;
    /** Button for selecting a game file to play.*/
    public Button loadGameBtn;

    /** Game manager provides functions for loading/saving game files/states.*/
    public GameManager gameManager = new GameManager();
    /** List of stop words to filter out of user input when processing commands.*/
    private final List<String> stopwords = Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now");

    /** Grammar defined by the currently loaded game, determines valid input.
     * @see Game*/
    private HashMap<String, Action> grammar = new HashMap<>();

    /** The stage for holding engine nodes.*/
    public static Stage stage;
    /** Current engine state determining what input is valid.*/
    private EngineState state = EngineState.NOT_LOADED;
    /** The currently loaded game.*/
    private Game game;
    /** Copy of loaded game to validate newly loaded states.*/
    private Game oldGame;
    /** Reference to current game's player.*/
    private Player player;
    /** Number of turns elapsed in game.*/
    private int turn = 0;

    /** Initialises the window, configuring the interface of the engine. Sets the event of
     * textEntryTa to process input after ENTER key is pressed.*/
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

    /** Determines the output as a result of user input. Return value primarily controlled
     * by the engine's current state.
     * @param input The user's command to be processed by the engine.
     * @return String Describes the effects of the player's command.
     * @see EngineState*/
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

    /** Parses user input using NLP to determine effects on game.
     * @param input The user's command to be processed.
     * @return String Describes the effects of the player's command.*/
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

    /** Executes a command after initial processing has been completed.
     * @param args The NL processed user input, transformed into an ArrayList of strings.
     * @return String Describes the effects of the player's command.*/
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

    /** Called when the user clicks the load game button. Passes request to the game manager.
     * @throws IOException if file cannot be read.
     * @see GameManager*/
    public void loadGame() throws IOException {
        Stage stage = (Stage) loadGameBtn.getScene().getWindow();
        game = gameManager.loadGameFile(stage); //opens file selection and loads selected file
        oldGame = new Game(game); //store a copy of the original game parameters
        player = game.getPlayer();

        if (game != null ) { //a game has been loaded
            grammar = game.getGrammar();
            gameTextTa.appendText("\n\nGame loaded successfully. Enjoy playing " + game.getTitle() + "!\n" +
                    "Enter !help for additional information.\n\n");
            state = EngineState.PLAYING;
        }
        else {
            gameTextTa.appendText("\n\nUnable to load game. Please check that the file is correct.\n\n");
        }
    }

    /** Called when the user clicks the save game button. Passes request to the game manager.
     * @throws IllegalSaveStateException if game is in a non-saveable state.
     * @throws IOException if file cannot be written to.
     * @see GameManager*/
    public void saveGameState() throws IllegalSaveStateException, IOException {
        switch (state) {
            case PLAYING: {
                gameManager.saveGameState(oldGame, game, stage);
                gameTextTa.appendText("\n\nGame saved successfully.\n\n");
                break;
            }
            case NOT_LOADED: {
                throw new IllegalSaveStateException("\n\nNo game has been loaded to save.\n\n");
            }
            default:
                throw new IllegalSaveStateException("\n\nGame cannot be saved after a win or loss.\n\n");
        }
    }

    /** Called when the user clicks the load state button. Passes request to the game manager.
     * @throws IOException if file cannot be read.
     * @throws FileNotSelectedException if user exits without selecting a file.
     * @throws IllegalSaveStateException if state is not compatible with current game file.
     * @see GameManager*/
    public void loadGameState() throws IOException, FileNotSelectedException, IllegalSaveStateException {
        if (state == EngineState.NOT_LOADED) {
            throw new IllegalSaveStateException("No game loaded, please select a game first.");
        } else {
            game = gameManager.loadGameState(oldGame, stage); //try to load a game state
            player = game.getPlayer();
            gameTextTa.appendText("\n\nSave game loaded successfully. You are in " + player.getCurrentRoom().getName() + "\n\n");
            state = EngineState.PLAYING;
            turn = player.getTurnCount();
        }
    }

    /** Validates if the user has specified a valid item before passing it as an argument.
     * Uses WordBuilderTools to create a list of possible items in user input.
     * @param input The user input to be searched for and validated.
     * @return Item The item found by the method. Can be null if not found.
     * @see WordBuilderTools
     * @see Item*/
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

    /** Checks all enemies in the current room for a response after player turn has incremented.
     * @return String Describes the actions of each enemy.
     * @see Enemy*/
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
