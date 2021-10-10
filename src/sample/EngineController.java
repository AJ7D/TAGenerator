package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class EngineController {

    public TextField textEntryTa;
    public VBox gameTextVbox;
    public ScrollPane gameTextSp;
    public TextArea gameTextTa;

    @FXML
    private void initialize() {
        textEntryTa.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {
                    String text = textEntryTa.getText();
                    gameTextTa.appendText(text + "\n\n");
                    textEntryTa.clear();
                }
            }
        });
    }

    public void parseInput(String input) {

    }
}
