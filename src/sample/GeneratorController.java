package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class GeneratorController {

    public static Stage stage;
    private static Game newGame;
    private int spOffset = 0;

    @FXML
    private TextField nameEntryTF;
    @FXML
    private Button newRoomBtn;
    @FXML
    private AnchorPane objectAnchorPane;
    @FXML
    private Button newItemBtn;
    @FXML
    private Button nExitBtn;
    @FXML
    private Button eExitBtn;
    @FXML
    private Button sExitBtn;
    @FXML
    private Button wExitBtn;
    @FXML
    private Button editRoomBtn;
    @FXML
    private Text selectedRoomTxt;
    @FXML
    private Button addItemRoomBtn;
    @FXML
    private Button saveReturnBtn;
    @FXML
    private ComboBox<String> startRoomCbx;

    @FXML
    private void initialize() {
        newGame = new Game("New game");
        nameEntryTF.setText(newGame.getTitle());

        callUpdate();
        startRoomCbx.getSelectionModel().selectFirst();
        Button but = (Button) objectAnchorPane.getChildren().get(0);
        buttonClick(but);
    }

    //TODO potentially merge these 3 functions
    @FXML
    private void updateRoom(MouseEvent event) throws IOException {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("roomconfig.fxml"));
        Parent root = fxmlLoader.load();
        RoomConfigController controller = fxmlLoader.getController();

        if (event.getSource().equals(editRoomBtn)) {
            String buttonId = selectedRoomTxt.getText();
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

    @FXML
    private void updateItem(MouseEvent event) throws IOException {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("itemconfig.fxml"));
        Parent root = fxmlLoader.load();
        ItemConfigController controller = fxmlLoader.getController();
        String buttonId = ((Node) event.getSource()).getId();

        if (!event.getSource().equals(newItemBtn)) {
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

    @FXML
    private void updateExit(MouseEvent event) throws IOException {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("exitconfig.fxml"));
        Parent root = fxmlLoader.load();
        ExitConfigController controller = fxmlLoader.getController();

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

    @FXML
    private void roomDisplayBar(MouseEvent event) {
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

    @FXML
    private void populateScrollPane() {
        objectAnchorPane.getChildren().clear();
        spOffset = 0;
        if (newGame.getGameMap().size() == 0) {
            return;
        }
        generateRoomsItems();
        objectAnchorPane.setPrefHeight(spOffset+20);
    }

    @FXML
    private void populateStartingRoomCombo() {
        ArrayList<String> rooms = new ArrayList<>();
        for (Room r : newGame.getGameMap()) {
            rooms.add(r.getName());
        }
        startRoomCbx.getItems().setAll(rooms);
    }

    @FXML
    private void generateRoomsItems() {
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
                        updateItem(event);
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

    @FXML
    private void buttonClick(Button but) {
        but.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, but.getLayoutX(), but.getLayoutY(), but.getLayoutX(), but.getLayoutY(),
                MouseButton.PRIMARY, 1, true, true, true, true, true,
                true, true, true, true, true, null));
    }

    @FXML
    private void switchScene(MouseEvent event) throws IOException {
        String fxml = "sample.fxml";
        if (event.getSource().equals(saveReturnBtn)) {
            fxml = "sample.fxml";
            setGameParams();
            newGame.saveGameData();
        }

        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void setGameParams() {
        newGame.setTitle(nameEntryTF.getText());
        newGame.setStartingRoom(newGame.getRoom(startRoomCbx.getValue()));
    }

    public void callUpdate() {
        populateScrollPane();
        populateStartingRoomCombo();
    }

    public static Game getNewGame() { return newGame; }

    public void newRoomDisplay(String string) {
        Button button = (Button) objectAnchorPane.lookup("#" +string);
        buttonClick(button);
    }
}
