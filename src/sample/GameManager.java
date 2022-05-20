package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

/** Class for handling game writing/reading to and from files.*/
public class GameManager { //methods for saving/loading game configuration/save states

    /** Saves game configuration as a game file, not to be confused with a game save state.
     * @param game The game to write to file.
     * @param stage The stage in which the file chooser will be initialised.
     * @return boolean Returns true if game file was saved successfully.
     * @throws IOException if file could not be written to.*/
    public boolean saveGameConfig(Game game, Stage stage) throws IOException {
        FileChooser fileChooser = new FileChooser(); //open new file chooser
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir"))); //set initial directory for file search
        fileChooser.setInitialFileName(game.getTitle() + ".xml"); //file format type
        fileChooser.setTitle("Save Game"); //title indicating purpose of file chooser
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game file", "*.xml"));

        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) { //user selected a file, didn't terminate process
            game.saveGameData(selectedFile); //try writing game data to file
            return true; //save was successful
        }
        return false; //save failed
    }

    /** Loads game configuration into the generator, not to be confused with a game save state.
     * @param stage The stage in which the file chooser will be initialised.
     * @return Game The game chosen to be loaded.
     * @throws IOException if file could not be read.*/
    public Game loadGameFile(Stage stage) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game file", "*.xml"));

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) {
            return null;
        }
        return readGameData(selectedFile);
    }

    /** Loads game configuration into the engine, not to be confused with a game save state.
     * @param file The file to be read.
     * @return Game The game chosen to be loaded.
     * @throws IOException if file could not be read.*/
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
                System.out.println("ERROR: Cannot read game file. (" + exception + ")");
                return null; //user has acknowledged error message
            }
            return null;
        }
    }

    /** Saves game state read from the engine, not to be confused with a game configuration file.
     * @param stage The stage in which the file chooser will be initialised.
     * @param state The state of the game.
     * @param initial The initial configuration of the game, for validating the save state later.
     * @throws IOException if file could not be written.*/
    public void saveGameState(Game initial, Game state, Stage stage) throws IOException {
        //save the state of a game file for reloading later
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setInitialFileName(initial.getTitle() + " SAVE.xml");
        fileChooser.setTitle("Save Game State");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game save file", "*.xml"));

        try {
            File selectedFile = fileChooser.showSaveDialog(stage);
            if (selectedFile == null)
                throw new FileNotSelectedException();
            FileOutputStream fileOutputStream
                    = new FileOutputStream(selectedFile);
            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(new GameSaveFile(initial, state));
            objectOutputStream.flush();
            objectOutputStream.close();

        } catch (FileNotSelectedException e) {
            e.printStackTrace();
        }
    }

    /** Loads game state into the engine, not to be confused with a game configuration file.
     * @param game The game to be loaded.
     * @param stage The stage in which the file chooser will be initialised.
     * @return Game The game state chosen to be loaded.
     * @throws IOException if file could not be read.
     * @throws FileNotSelectedException if user did not select a file to load.
     * @throws IllegalSaveStateException if loaded state does not match loaded game's configuration.*/
    public Game loadGameState(Game game, Stage stage) throws IOException, FileNotSelectedException, IllegalSaveStateException {
        //load a saved game state
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game save file", "*.xml"));

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) {
            throw new FileNotSelectedException();
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
                throw new IllegalSaveStateException("Failed to load save file.");
            }

            if (!gameSaveFile.verifyState(gameSaveFile.getInitialConfig(), game)) {
                throw new IllegalSaveStateException("Save file is not for this game.");
            }
            loadedState = gameSaveFile.getSavedConfig();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new IllegalSaveStateException("File could not be loaded. Please check that it is a save state.");
        }
        return loadedState;
    }
}
