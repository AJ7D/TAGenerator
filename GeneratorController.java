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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GeneratorController {

    static final int ITEMWIDTH = 631;
    static final int ITEMHEIGHT = 400;

    static final int ENEMYWIDTH = 400;
    static final int ENEMYHEIGHT = 400;

    static final int ITEM_LIST_INCREMENT = 20;

    public static Stage stage;
    private static Game newGame;
    private int spOffset = 0;
    private final GameManager gameManager = new GameManager();

    private Room selectedRoom;

    @FXML private TextField nameEntryTF;
    @FXML private AnchorPane objectAnchorPane;
    @FXML private Button newItemBtn;
    @FXML private Button newEnemyBtn;
    @FXML private Button nExitBtn;
    @FXML private Button eExitBtn;
    @FXML private Button sExitBtn;
    @FXML private Button wExitBtn;
    @FXML private Button editRoomBtn;
    @FXML private Text selectedRoomTxt;
    @FXML private ComboBox<Room> startRoomCbx;
    @FXML private VBox inventoryVbox;
    @FXML private Button exitNoSaveBtn;

    @FXML
    private void initialize() {
        //Initialise default configuration - empty game
        newGame = new Game("New game");
        nameEntryTF.setText(newGame.getTitle());

        updateInterfaceParameters();

        UITools uit = new UITools();
        uit.configureComboboxRoom(startRoomCbx);
        startRoomCbx.getSelectionModel().selectFirst();
        Button but = (Button) objectAnchorPane.getChildren().get(0);
        buttonClick(but);
        selectedRoom = newGame.getRoom(Long.parseLong(objectAnchorPane.getChildren().get(0).getId()));
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
            Long buttonId = selectedRoom.getId();
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
    private void deleteRoom() {
        if (newGame.getGameMap().size() == 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Room deletion failure");
            alert.setContentText("You cannot delete this room as it is the last room.");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.isPresent() && option.get() == ButtonType.OK) {
                return;
            }
        }

        Room room = selectedRoom;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Room deletion confirmation");
        if (room.hasItems()) {
            alert.setContentText("This room has items that will be deleted. Delete anyway?");
        }
        else {
            alert.setContentText("Are you sure you want to delete " + selectedRoom.getName() + "?");
        }

        Optional<ButtonType> option = alert.showAndWait();

        if (option.isPresent() && option.get() == ButtonType.OK) {
            newGame.deleteRoom(room);
            updateInterfaceParameters();
            if (startRoomCbx.getValue().getName().equals(room.getName())) {
                startRoomCbx.getSelectionModel().selectFirst();
            }
            String id = objectAnchorPane.getChildren().get(0).getId();
            newRoomDisplay(id);
            selectedRoom = newGame.getRoom(Long.parseLong(id));
        }
        if (option.isPresent() && option.get() == ButtonType.CANCEL) {
            System.out.println("Not deleting room");
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
            Scene dialogScene = new Scene(root, ITEMWIDTH, ITEMHEIGHT);
            dialog.setScene(dialogScene);
            dialog.show();
        }
        catch (Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    @FXML
    private void updateEnemy(MouseEvent event) throws IOException {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("enemyconfig.fxml"));
        Parent root = fxmlLoader.load();
        EnemyConfigController controller = fxmlLoader.getController();
        String buttonId = ((Node) event.getSource()).getId();

        if (!event.getSource().equals(newEnemyBtn)) {
            controller.loadEnemy(buttonId);
        }

        controller.setGeneratorController(this);

        try {
            Scene dialogScene = new Scene(root, ENEMYWIDTH, ENEMYHEIGHT);
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
        controller.loadRoom(selectedRoom, dir);

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
        selectedRoom = newGame.getRoom(Long.parseLong(btn.getId()));
        selectedRoomTxt.setText(selectedRoom.getName());

        Button[] buttons = {nExitBtn, wExitBtn, eExitBtn, sExitBtn};
        int i = 0;
        for (Room ex : selectedRoom.getExits()) {
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
        objectAnchorPane.setPrefHeight(spOffset+ITEM_LIST_INCREMENT);
    }

    @FXML
    private void populateStartingRoomCombo() {
        startRoomCbx.getItems().setAll(newGame.getGameMap());
        startRoomCbx.setValue(newGame.getStartingRoom());
    }

    @FXML
    private void generateRoomsItems() {
        for (Room r : newGame.getGameMap()) {
            Button b = new Button(r.getName());
            b.setId(String.valueOf(r.getId()));
            b.setOnMouseClicked(this::roomDisplayBar);
            b.setLayoutY(spOffset);
            b.getStyleClass().add("buttonscroll");
            spOffset = spOffset + ITEM_LIST_INCREMENT;
            objectAnchorPane.getChildren().add(b);

            recItemSearch(r.getEntities(), 1);
        }
    }

    private void recItemSearch(Collection<? extends Entity> entityList, int depth) {
        for (Entity e : entityList) {
            if (e instanceof Item) {
                Button b = new Button(e.getName());
                b.setId(String.valueOf(e.getId()));
                b.setOnMouseClicked(event -> {
                    try {
                        updateItem(event);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });
                configBtn(b, ITEM_LIST_INCREMENT*depth);
            }
            else if (e instanceof Enemy) {
                Button b = new Button(e.getName());
                b.setId(String.valueOf(e.getId()));
                b.setOnMouseClicked(event -> {
                    try {
                        updateEnemy(event);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });
                configBtn(b, ITEM_LIST_INCREMENT);
                List<Item> inventory = ((Enemy) e).getInventory().getContents();
                if (!inventory.isEmpty()) {
                    recItemSearch(inventory, depth+1);
                }
            }
            if (e instanceof Container) {
                ArrayList<Item> inventory = ((Container) e).getItems();
                if (!inventory.isEmpty()) {
                    recItemSearch(inventory, depth+1);
                }
            }
        }
    }

    public void configBtn(Button btn, int xoffset) {
        btn.setLayoutY(spOffset);
        btn.setLayoutX(xoffset);
        btn.getStyleClass().add("buttonscroll");
        spOffset = spOffset + ITEM_LIST_INCREMENT;
        objectAnchorPane.getChildren().add(btn);
    }
    @FXML
    private void populateInventoryItems() {
        inventoryVbox.getChildren().clear();
        List<Item> inventory = newGame.getPlayer().getInventory().getContents();
        for (Item i : inventory) {
            Button b = new Button(i.getName());
            b.setId(String.valueOf(i.getId()));
            b.setOnMouseClicked(event -> {
                try {
                    updateItem(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            b.getStyleClass().add("buttonscroll");
            inventoryVbox.getChildren().add(b);
        }
    }

    @FXML
    private void buttonClick(Button but) {
        //simulate button click for button arg
        but.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, but.getLayoutX(), but.getLayoutY(), but.getLayoutX(), but.getLayoutY(),
                MouseButton.PRIMARY, 1, true, true, true, true, true,
                true, true, true, true, true, null));
    }

    @FXML
    private void saveAndQuit(MouseEvent event) throws IOException {
        //TODO fix
        setGameParams();
        if (!gameManager.saveGameConfig(newGame, stage)) {
            System.out.println("ERROR: Failed to save.");
            return;
        }
        quit();
    }

    private void quit() throws IOException {
        String fxml = "sample.fxml";

        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stage = (Stage) exitNoSaveBtn.getScene().getWindow();
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void quitWithWarning() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit confirmation");
        alert.setContentText("Are you sure you want to quit without saving?");

        Optional<ButtonType> option = alert.showAndWait();

        if (option.isPresent() && option.get() == ButtonType.OK) {
            quit();
        }
    }

    @FXML
    private void loadGameConfig() throws IOException, ClassNotFoundException {
        Game loaded = gameManager.loadGameFile(stage);

        if (loaded != null) {
            newGame = loaded;
            nameEntryTF.setText(newGame.getTitle());
            updateInterfaceParameters();
        }
    }

    @FXML
    private void setGameParams() {
        newGame.setTitle(nameEntryTF.getText());
        newGame.setStartingRoom(startRoomCbx.getValue());
    }

    public void updateInterfaceParameters() {
        //update ui when new data is added
        populateScrollPane();
        populateStartingRoomCombo();
        populateInventoryItems();
    }

    public static Game getNewGame() { return newGame; }

    public void newRoomDisplay(String string) {
        //simulate button click to update ui, call from external window
        Button button = (Button) objectAnchorPane.lookup("#" +string);
        buttonClick(button);
    }
}
