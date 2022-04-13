package sample;

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

public class Controller {
    public Pane pane;

    public static Stage stage;
    public Button generatorBtn;
    public Button gameBtn;
    public Text titleTxt;

    public void switchScene(MouseEvent event) throws IOException {
        String fxml;
        if (event.getSource().equals(generatorBtn))
            fxml = "generator.fxml";
        else
            fxml = "engine.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.show();
    }
}
