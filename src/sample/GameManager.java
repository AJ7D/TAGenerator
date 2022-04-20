package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

public class GameManager { //methods for saving/loading game configuration/save states

    public boolean saveGameConfig(Game game, Stage stage) throws IOException {
        FileChooser fileChooser = new FileChooser(); //open new file chooser
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir"))); //set initial directory for file search
        fileChooser.setInitialFileName(game.getTitle() + ".txt"); //file format type
        fileChooser.setTitle("Save Game"); //title indicating purpose of file chooser
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game file", "*.txt"));

        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) { //user selected a file, didn't terminate process
            game.saveGameData(selectedFile); //try writing game data to file
            return true; //save was successful
        }
        return false; //save failed
    }

    public Game loadGameFile(Stage stage) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game file", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) {
            return null;
        }
        return readGameData(selectedFile);
    }

    private Game readGameData(File file) throws IOException {
        FileInputStream fileInputStream
                = new FileInputStream(file); //select file as input stream
        try {
            ObjectInputStream objectInputStream
                    = new ObjectInputStream(fileInputStream);
            Game game = (Game) objectInputStream.readObject(); //get object data
            objectInputStream.close();

            if (game == null) {
                return null;
            }

            Player player = game.getPlayer();
            player.setCurrentRoom(game.getStartingRoom());
            return game;
        }
        catch (Exception exception) { //general error handling for now
            Alert alert = new Alert(Alert.AlertType.INFORMATION); //display alert informing player game was not loaded
            alert.setTitle("Failed to load game");
            alert.setContentText("ERROR: Game could not be loaded. Please check that the file is correct.");

            Optional<ButtonType> option = alert.showAndWait(); //wait for user response

            if (option.isPresent() && option.get() == ButtonType.OK) {
                return null; //user has acknowledged error message
            }
            System.out.println("ERROR: Cannot read game file. (" + exception + ")");
            return null;
        }
    }

    public void saveGameState(Game initial, Game state, Stage stage) throws IOException {
        //save the state of a game file for reloading later
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setInitialFileName(initial.getTitle() + " SAVE.txt");
        fileChooser.setTitle("Save Game State");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game save file", "*.txt"));

        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            FileOutputStream fileOutputStream
                    = new FileOutputStream(selectedFile);
            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(new GameSaveFile(initial, state));
            objectOutputStream.flush();
            objectOutputStream.close();
        }
    }

    public Game loadGameState(Game game, Stage stage) throws IOException {
        //load a saved game state
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game save file", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) {
            return null;
        }

        Game loadedState = null;
        FileInputStream fileInputStream
                = new FileInputStream(selectedFile);
        try {
            ObjectInputStream objectInputStream
                    = new ObjectInputStream(fileInputStream);
            GameSaveFile gameSaveFile = (GameSaveFile) objectInputStream.readObject();
            objectInputStream.close();

            if (gameSaveFile == null) {
                return null;
            }

            if (!gameSaveFile.verifyState(gameSaveFile.getInitialConfig(), game)) {
                throw new IllegalSaveStateException("Save file is not for this game.");
            }
            loadedState = gameSaveFile.getSavedConfig();
        } catch (ClassNotFoundException | IllegalSaveStateException e) {
            e.printStackTrace();
        }
        return loadedState;
    }
}
