package sample;

import java.io.Serializable;
import java.util.ArrayList;

public class GameSaveFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private Game initialConfig; //game file's initial configuration
    private Game savedConfig; //updated game parameters

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

    public Game getInitialConfig() {
        return initialConfig;
    }

    public Game getSavedConfig() {
        return savedConfig;
    }

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
