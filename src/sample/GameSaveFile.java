package sample;

import java.io.Serializable;
import java.util.ArrayList;

public class GameSaveFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private Game initialConfig;
    private Game savedConfig;

    public GameSaveFile(Game initial, Game state) {
        this.initialConfig = initial;
        this.savedConfig = state;
        try {
            verifyState(initialConfig, savedConfig);
        } catch (IllegalSaveStateException e) {
            e.printStackTrace();
        }
    }

    public Game getInitialConfig() {
        return initialConfig;
    }

    public Game getSavedConfig() {
        return savedConfig;
    }

    public boolean verifyState(Game initial, Game state) throws IllegalSaveStateException {
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
        return true;
    }
}
