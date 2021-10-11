package sample;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class GameManager {

    public boolean saveGameConfig(Game game, Stage stage) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setInitialFileName(game.getTitle() + ".txt");
        fileChooser.setTitle("Save Game");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game file", "*.txt"));

        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            game.saveGameData(selectedFile);
            return true;
        }
        return false;
    }

    public Game loadGameFile(Stage stage) throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game file", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(stage);
        Game game = readGameData(selectedFile);
        return game;
    }

    private Game readGameData(File file) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream
                = new FileInputStream(file);
        try {
            ObjectInputStream objectInputStream
                    = new ObjectInputStream(fileInputStream);
            Game game = (Game) objectInputStream.readObject();
            objectInputStream.close();

            if (game == null) {
                return null;
            }

            Player player = game.getPlayer();
            player.setCurrentRoom(game.getStartingRoom());
            return game;
        }
        catch (Exception exception) {
            System.out.println("ERROR: Cannot read game file. (" + exception + ")");
            return null;
        }
    }
}
