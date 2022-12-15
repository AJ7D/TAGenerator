package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class for the main menu of the application.
 */
public class MainController {
    /** Pane for holding main menu nodes.*/
    @FXML
    private Pane pane;

    /** The main menu stage.*/
    @FXML
    private static Stage stage;
    /** Button that takes the user to the generator screen.*/
    @FXML
    private Button generatorBtn;
    /** Button that takes the user to the engine screen.*/
    @FXML
    private Button gameBtn;
    /** Displays the main menu title.*/
    @FXML
    private Text titleTxt;

    /** Handles scene changing when the user clicks the generator or engine button.
     * @throws IOException if scene contents cannot be loaded.
     * @param event Determines the source button of the mouse event.*/
    public void switchScene(MouseEvent event) throws IOException {
        //main menu
        String fxml;
        if (event.getSource().equals(generatorBtn))
            fxml = "generator/generator.fxml"; //get generator fxml for designing games
        else
            fxml = "engine/engine.fxml"; //get engine fxml for play

        //load given fxml and apply to stage, then show
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.show();
    }
}
