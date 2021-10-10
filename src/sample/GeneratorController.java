package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class GeneratorController {
    public Pane pane;

    public static Stage stage;

    public TextField nameEntryTF;
    public static Game newGame;
    public Button newRoomBtn;
    public Text nameTxt;
    public ScrollPane objectScrollPane;
    public AnchorPane objectAnchorPane;
    public ToolBar objectToolBar;

    public int spOffset = 0;
    public Button newItemBtn;
    public Button nExitBtn;
    public Button eExitBtn;
    public Button sExitBtn;
    public Button wExitBtn;
    public Button editRoomBtn;
    public Text selectedRoomTxt;
    public Button addItemRoomBtn;

    @FXML
    private void initialize() {
        newGame = new Game("New game");
        nameEntryTF.setText(newGame.getTitle());
        newGame.createRoom("Room1", "This is a test room.", new Room[4]);
        //TODO remove test
        //newGame.createRoom("Room2", "Test room 2", new Room[4]);
        //newGame.getRoom("Room1").addExit(Direction.NORTH, newGame.getRoom("Room2"));
        populateScrollPane();

        Button but = (Button) objectAnchorPane.getChildren().get(0);
        buttonClick(but);
    }

    public void createNewObject(MouseEvent event) throws IOException {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("roomconfig.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        RoomConfigController controller = fxmlLoader.<RoomConfigController>getController();
        String buttonId = ((Node) event.getSource()).getId();

        if (event.getSource().equals(newRoomBtn)) {
            //do stuff
        }
        else {
            controller.loadRoom(buttonId);
        }

        controller.setGeneratorController(this);

        try {
            Scene dialogScene = new Scene(root, 400, 400);
            dialog.setScene(dialogScene);
            dialog.show();
        }
        catch (Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    public void createNewItem(MouseEvent event) throws IOException {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("itemconfig.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        ItemConfigController controller = fxmlLoader.<ItemConfigController>getController();
        String buttonId = ((Node) event.getSource()).getId();

        if (event.getSource().equals(newItemBtn)) {
            //do stuff
        }
        else {
            controller.loadItem(buttonId);
        }

        controller.setGeneratorController(this);

        try {
            Scene dialogScene = new Scene(root, 400, 400);
            dialog.setScene(dialogScene);
            dialog.show();
        }
        catch (Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    public void updateExit(MouseEvent event) throws IOException {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("exitconfig.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        ExitConfigController controller = fxmlLoader.<ExitConfigController>getController();

        Button button = ((Button) event.getSource());
        String buttonId = button.getId();

        String room = newGame.getRoom(selectedRoomTxt.getText()).getName();
        Direction dir = null;

        if (button.equals(nExitBtn)) {
            dir = Direction.NORTH;
        }
        else if (button.equals(wExitBtn)) {
            dir = Direction.WEST;
        }
        else if (button.equals(eExitBtn)) {
            dir = Direction.EAST;
        }
        else if (button.equals(sExitBtn)) {
            dir = Direction.SOUTH;
        }

        //switch case button -> button objects defined
        controller.loadRoom(room, dir);

        controller.setGeneratorController(this);

        try {
            Scene dialogScene = new Scene(root, 400, 400);
            dialog.setScene(dialogScene);
            dialog.show();
        }
        catch (Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    public void roomDisplayBar(MouseEvent event) {
        Button btn = (Button) event.getSource();
        Room r = newGame.getRoom(btn.getId());
        selectedRoomTxt.setText(r.getName());

        Button[] buttons = {nExitBtn, wExitBtn, eExitBtn, sExitBtn};
        int i = 0;
        for (Room ex : r.getExits()) {
            if (ex != null) {
                buttons[i].setText(ex.getName());
                buttons[i].setId(ex.getName());
            }
            else {
                buttons[i].setText("None");
                buttons[i].setId("no_exit_" + i);
            }
            buttons[i].setOnMouseClicked(event2 -> {
                try {
                    updateExit(event2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            i++;
        }
    }

    public void populateScrollPane() {
        objectAnchorPane.getChildren().clear();
        spOffset = 0;
        if (newGame.getGameMap().size() == 0) {
            return;
        }
        generateRoomsItems();
        objectAnchorPane.setPrefHeight(spOffset+20);
    }

    public void generateRoomsItems() {
        for (Room r : newGame.getGameMap()) {
            Button b = new Button(r.getName());
            b.setId(r.getName());
            b.setOnMouseClicked(this::roomDisplayBar);
            b.setLayoutY(spOffset);
            b.getStyleClass().add("buttonscroll");
            spOffset = spOffset + 20;
            objectAnchorPane.getChildren().add(b);

            for (Item i : r.getItems()) {
                Button b2 = new Button(i.getName());
                b2.setId(i.getName());
                b2.setOnMouseClicked(event -> {
                    try {
                        createNewItem(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                b2.setLayoutY(spOffset);
                b2.setLayoutX(20);
                b2.getStyleClass().add("buttonscroll");
                spOffset = spOffset + 20;
                objectAnchorPane.getChildren().add(b2);
            }
        }
    }

    public void callUpdate() {
        populateScrollPane();
    }

    public void buttonClick(Button but) {
        but.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, but.getLayoutX(), but.getLayoutY(), but.getLayoutX(), but.getLayoutY(),
                MouseButton.PRIMARY, 1, true, true, true, true, true,
                true, true, true, true, true, null));
    }

    public static Game getNewGame() { return newGame; }
}
