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

    private static final int MAX_STRING_LENGTH = 50;
    private static final int ITEMWIDTH = 690;
    private static final int ITEMHEIGHT = 400;

    private static final int ENEMYWIDTH = 400;
    private static final int ENEMYHEIGHT = 400;

    private static final int ENTITY_INCREMENTAL_OFFSET = 20;

    public static Stage stage;
    private static Game newGame;
    private int spOffset = 0;
    private final GameManager gameManager = new GameManager();

    private Room selectedRoom;

    @FXML private TextField nameEntryTF;
    @FXML private TextField playerEntryTF;
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
    @FXML private ComboBox<Room> winRoomCbx;
    @FXML private VBox inventoryVbox;
    @FXML private Button exitNoSaveBtn;

    @FXML
    private void initialize() {
        //Initialise default configuration - empty game
        newGame = new Game("New game");
        nameEntryTF.setText(newGame.getTitle());
        playerEntryTF.setText(newGame.getPlayer().getName());

        updateInterfaceDisplay();

        UITools.configureComboboxRoom(startRoomCbx); //configure startroomcbx to hold room references
        startRoomCbx.getSelectionModel().selectFirst(); //select first element for starting room by default
        startRoomCbx.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                newGame.setStartingRoom(newValue);
                System.out.println("starting room = " + newGame.getStartingRoom().getName());
            }
        }); //update game's starting room when combo box selection is changed

        UITools.configureComboboxRoom(winRoomCbx);
        winRoomCbx.getSelectionModel().selectFirst();
        winRoomCbx.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                newGame.setWinCondition(newValue);
                System.out.println("win condition = " + newGame.getWinCondition().getName());
            }
        });

        Button room1Button = (Button) objectAnchorPane.getChildren().get(0);
        simulateButtonClick(room1Button); //simulate button click to update display

        selectedRoom = newGame.getRoom(Long.parseLong(objectAnchorPane.getChildren().get(0).getId()));
        //set selected room to the default empty room created on new game creation
    }

    @FXML
    private void updateRoom(MouseEvent event) throws IOException {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);

        //load room config fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("roomconfig.fxml"));
        Parent root = fxmlLoader.load();
        RoomConfigController controller = fxmlLoader.getController();

        if (event.getSource().equals(editRoomBtn)) {
            Long buttonId = selectedRoom.getId(); //existing room selected for editing, load it into new window
            controller.loadRoom(buttonId);
        }

        controller.setGeneratorController(this); //pass generator reference to new window

        try {
            Scene dialogScene = new Scene(root, 400, 400); //create window using configurations
            dialog.setScene(dialogScene);
            dialog.show();
        }
        catch (Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    @FXML
    private void deleteRoom() throws InvalidGameConfigError {
        if (newGame.getGameMap().size() == 1) { //
            throw new InvalidGameConfigError("You cannot delete this room as it is the last room.");
            }

        //confirm that user wants to delete room
        Room room = selectedRoom;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Room deletion confirmation");
        if (room.hasItems()) { //alert player that items will be deleted
            alert.setContentText(selectedRoom.getName() + " has items that will be deleted. Delete anyway?");
        }
        else {
            alert.setContentText("Are you sure you want to delete " + selectedRoom.getName() + "?");
        }

        Optional<ButtonType> option = alert.showAndWait();

        if (option.isPresent() && option.get() == ButtonType.OK) { //user agrees to delete room
            newGame.deleteRoom(room);
            updateInterfaceDisplay();
            if (startRoomCbx.getValue().getId() == room.getId()) {
                //update combobox to remove room from selection
                startRoomCbx.getSelectionModel().selectFirst();
            }
            if (winRoomCbx.getValue().getId() == room.getId())
                winRoomCbx.getSelectionModel().selectFirst();
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
        //load fxml and create window
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

        controller.setGeneratorController(this); //pass this generator reference to new window

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
        //load fxml and create window
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

        controller.setGeneratorController(this); //pass this generator reference to new window

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
        //load fxml and create window
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("exitconfig.fxml"));
        Parent root = fxmlLoader.load();
        ExitConfigController controller = fxmlLoader.getController();

        Button button = ((Button) event.getSource()); //determine direction selected for exit editing

        Direction dir = null;

        if (button.equals(nExitBtn)) { //get direction associated with button
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

        controller.setGeneratorController(this); //pass generator reference to new window

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
    private void updateRoomDisplayBar(MouseEvent event) {
        Button btn = (Button) event.getSource(); //get button that was clicked
        selectedRoom = newGame.getRoom(Long.parseLong(btn.getId())); //find room associated with button through id
        selectedRoomTxt.setText(selectedRoom.getName()); //update display bar to show selected room's name

        Button[] buttons = {nExitBtn, wExitBtn, eExitBtn, sExitBtn}; //direction buttons for iterating
        int i = 0;
        for (Room ex : selectedRoom.getExits()) { //update button texts
            if (ex != null) {
                buttons[i].setText(ex.getName()); //display connected room's name
                buttons[i].setId(ex.getName());
            }
            else {
                buttons[i].setText("None"); //no room connected in this direction
                buttons[i].setId("no_exit_" + i);
            }
            buttons[i].setOnMouseClicked(event2 -> { //update button's event on reconfig
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
        //populate the scroll pane that displays entities in game
        objectAnchorPane.getChildren().clear();
        spOffset = 0; //start with no offset
        if (newGame.getGameMap().size() == 0) {
            return;
        }
        generateRoomsItems();
        objectAnchorPane.setPrefHeight(spOffset+ ENTITY_INCREMENTAL_OFFSET);
    }

    @FXML
    private void populateRoomCombos() {
        //updates the room combo boxes to show all rooms in game map
        startRoomCbx.getItems().setAll(newGame.getGameMap());
        startRoomCbx.setValue(newGame.getStartingRoom());

        winRoomCbx.getItems().setAll(newGame.getGameMap());
        winRoomCbx.setValue(newGame.getWinCondition());
    }

    @FXML
    private void generateRoomsItems() {
        //loops through game map to display each room and contained entities
        for (Room r : newGame.getGameMap()) {
            Button b = new Button(r.getName()); //create button for this room, signed as room's name
            b.setId(String.valueOf(r.getId())); //set button id to this room's unique id
            b.setOnMouseClicked(this::updateRoomDisplayBar); //button event passes this room to update room display
            b.setLayoutY(spOffset);
            b.getStyleClass().add("buttonscroll"); //format button using css
            spOffset = spOffset + ENTITY_INCREMENTAL_OFFSET; //update offset for next element
            objectAnchorPane.getChildren().add(b); //add this button to the anchor pane

            recItemSearch(r.getEntities(), 1); //use recursion to display all room's items
        }
    }

    private void recItemSearch(Collection<? extends Entity> entityList, int depth) {
        //recursive method for displaying all items in a collection, allowing traversal
        //through all item holders in a room, with visual offsets
        for (Entity e : entityList) {
            if (e instanceof Item) {
                Button b = new Button(e.getName()); //create button with entity name
                b.setId(String.valueOf(e.getId())); //set button id to entity unique id
                b.setOnMouseClicked(event -> { //use unique id to set this button's event call
                    try {
                        updateItem(event); //for loading this item into item config
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });
                //display entity on interface relative to the number of other entities it is contained within
                configBtn(b, ENTITY_INCREMENTAL_OFFSET *depth);
            }
            else if (e instanceof Enemy) {
                Button b = new Button(e.getName()); //create button with enemy name
                b.setId(String.valueOf(e.getId())); //set button id to enemy's unique id
                b.setOnMouseClicked(event -> { //use unique id to set this button's event call
                    try {
                        updateEnemy(event); //for loading this item into item config
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });
                configBtn(b, ENTITY_INCREMENTAL_OFFSET);
                //enemy can hold items, so check if we need to call recursively on this collection
                List<Item> inventory = ((Enemy) e).getInventory().getContents();
                if (!inventory.isEmpty()) {
                    recItemSearch(inventory, depth+1); //increment depth for display purposes
                }
            }
            if (e instanceof Container) { //if item is a container, check if recursion is required
                ArrayList<Item> inventory = ((Container) e).getItems();
                if (!inventory.isEmpty()) {
                    recItemSearch(inventory, depth+1);
                }
            }
        }
    }

    public void configBtn(Button btn, int xoffset) {
        //helper method for configuring appearance/placement of buttons generated in generateRoomItems~related methods
        btn.setLayoutY(spOffset);
        btn.setLayoutX(xoffset);
        btn.getStyleClass().add("buttonscroll");
        spOffset = spOffset + ENTITY_INCREMENTAL_OFFSET;
        objectAnchorPane.getChildren().add(btn);
    }

    @FXML
    private void populateInventoryItems() {
        inventoryVbox.getChildren().clear();
        List<Item> inventory = newGame.getPlayer().getInventory().getContents();
        for (Item i : inventory) { //create buttons for each item in player inventory
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
    private void simulateButtonClick(Button but) {
        //simulate button click for button arg
        but.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, but.getLayoutX(), but.getLayoutY(), but.getLayoutX(), but.getLayoutY(),
                MouseButton.PRIMARY, 1, true, true, true, true, true,
                true, true, true, true, true, null));
    }

    @FXML
    private void saveAndQuit(MouseEvent event) throws IOException {
        //save game and quit generator
        try {
            validateInputs();
            setGameParams();
            if (!gameManager.saveGameConfig(newGame, stage)) {
                System.out.println("ERROR: Failed to save.");
                return;
            }
            quit();
        } catch (InvalidInputException e) {
            System.out.println(e.toString());
        }
    }

    private void quit() throws IOException {
        //returns to the main menu by loading fxml/setting scene
        String fxml = "sample.fxml";

        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stage = (Stage) exitNoSaveBtn.getScene().getWindow();
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void quitWithWarning() throws IOException {
        //confirm that user wants to quit before quitting
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit confirmation");
        alert.setContentText("Are you sure you want to quit without saving?");

        Optional<ButtonType> option = alert.showAndWait();

        if (option.isPresent() && option.get() == ButtonType.OK) {
            quit();
        }
    }

    @FXML
    private void loadGameConfig() throws IOException {
        Game loaded = gameManager.loadGameFile(stage);
        //TODO add error handling
        if (loaded != null) {
            newGame = loaded;
            nameEntryTF.setText(newGame.getTitle());
            playerEntryTF.setText(newGame.getPlayer().getName());
            updateInterfaceDisplay();
        }
    }

    @FXML
    private void setGameParams() {
        //update generic game parameters on ui
        newGame.setTitle(nameEntryTF.getText());
        newGame.getPlayer().setName(playerEntryTF.getText());
    }

    public void updateInterfaceDisplay() {
        //update ui when new data is added
        populateScrollPane(); //updates scrollpane to show any new entities added/deleted
        populateRoomCombos(); //update combobox list of valid starting rooms
        populateInventoryItems(); //update user's starting items display
    }

    public static Game getNewGame() { return newGame; }

    public void newRoomDisplay(String string) {
        //simulate button click to update ui, call from external window
        Button button = (Button) objectAnchorPane.lookup("#" +string);
        simulateButtonClick(button);
    }

    private void validateInputs() throws InvalidInputException {
        if (nameEntryTF.getText().trim().length() > MAX_STRING_LENGTH ||
                nameEntryTF.getText().trim().length() == 0) {
            throw new InvalidInputException("Please enter a title between 0-50 characters.");
        }

        if (playerEntryTF.getText().trim().length() > MAX_STRING_LENGTH ||
                playerEntryTF.getText().trim().length() == 0) {
            throw new InvalidInputException("Please enter a player name between 0-50 characters.");
        }
    }
}
