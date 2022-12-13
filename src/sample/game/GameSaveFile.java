package sample.game;

import sample.engine.IllegalSaveStateException;
import sample.game.Game;

import java.io.Serializable;

/** Stores the saved state of a game file as well as the original configuration for validation purposes.
 * @serial */
public class GameSaveFile implements Serializable {
    /** The serial version UID of the game.*/
    private static final long serialVersionUID = 1L;
    /** The initial configuration of the game file before user input.*/
    private Game initialConfig; //game file's initial configuration
    /** The adjusted configuration of the game file after user input.*/
    private Game savedConfig; //updated game parameters

    /** Constructor for a game save file, taking initial config and save state config.
     * @param state The initial configuration of the game before user input.
     * @param initial The configuration of the game after user input to be saved.*/
    public GameSaveFile(Game initial, Game state) {
        this.initialConfig = initial;
        this.savedConfig = state;
        //ascertain that saved config is a legal reconfiguration of the initial game configuration
        try {
            verifyState(initialConfig, savedConfig);
        } catch (IllegalSaveStateException e) {
            e.printStackTrace(); //save file cannot be created, configuration is not legal
        }
    }

    /** Gets the initial configuration of the save file.
     * @return Game The initial configuration of the save file.*/
    public Game getInitialConfig() {
        return initialConfig;
    }

    /** Gets the updated configuration of the save file.
     * @return Game The updated configuration of the save file.*/
    public Game getSavedConfig() {
        return savedConfig;
    }

    /** Verifies that the initial configuration and updated configuration are compatible.
     * @param initial The initial game configuration.
     * @param state The updated game configuration to be checked.
     * @return boolean Returns true if configurations are compatible.
     * @throws IllegalSaveStateException if configurations are not compatible.*/
    public boolean verifyState(Game initial, Game state) throws IllegalSaveStateException {
        //checks essential parameters to find any discrepancies that lead to incompatibility
        if (!initial.getTitle().equals(state.getTitle()))
            throw new IllegalSaveStateException("Game data does not match (title).");
        if (!initial.getGameMap().equals(state.getGameMap()))
            throw new IllegalSaveStateException("Game data does not match (map).");
        if (!initial.getGameItems().equals(state.getGameItems()))
            throw new IllegalSaveStateException("Game data does not match (items).");
        if (!initial.getGameEnemies().containsAll(state.getGameEnemies()))
            throw new IllegalSaveStateException("Game data does not match (enemies).");
        if (!initial.getGrammar().keySet().equals(state.getGrammar().keySet())) {
            System.out.println(initial.getGrammar().keySet());
            System.out.println(state.getGrammar().keySet());
            throw new IllegalSaveStateException("Game data does not match (grammar).");
        }
        return true; //data is compatible
    }
}
