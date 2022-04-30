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

/** Generator controller for allowing users to configure a game file for playing.
 * Provides a user interface for adding rooms, items, enemies, etc. as well as saving
 * game config and loading an existing game file for further editing. Creates games
 * compatible with the engine.
 * @see Game,EngineController*/
public class GeneratorController {
    /** Maximum number of characters for game title.*/
    private static final int MAX_STRING_LENGTH = 50;
    /** Fixed width of generated item window.*/
    private static final int ITEMWIDTH = 690;
    /** Fixed height of generated item window.*/
    private static final int ITEMHEIGHT = 400;

    /** Fixed width of generated enemy window.*/
    private static final int ENEMYWIDTH = 400;
    /** Fixed height of generated enemy window.*/
    private static final int ENEMYHEIGHT = 400;

    /** Manually determines spacing of entities in the entity tree.*/
    private static final int ENTITY_INCREMENTAL_OFFSET = 20;

    /** The stage that holds JavaFX nodes.*/
    public static Stage stage;
    /** The game file that is being edited.*/
    private static Game newGame;
    /** Used to control entity indenting when entities are nested within each other.*/
    private int spOffset = 0;
    /** Tool used for saving/loading games cleanly.*/
    private final GameManager gameManager = new GameManager();

    /** Hold reference of selected room for displaying room attributes.*/
    private Room selectedRoom;

    /** Text field for user to enter game title.*/
    @FXML private TextField nameEntryTF;
    /** Text field for user to enter player name.*/
    @FXML private TextField playerEntryTF;
    /** Anchor pane for holding entity tree.*/
    @FXML private AnchorPane objectAnchorPane;
    /** Button for creating a new item window.*/
    @FXML private Button newItemBtn;
    /** Button for creating a new enemy window.*/
    @FXML private Button newEnemyBtn;
    /** Button for creating an exit to the north of selected room.*/
    @FXML private Button nExitBtn;
    /** Button for creating an exit to the east of selected room.*/
    @FXML private Button eExitBtn;
    /** Button for creating an exit to the south of selected room.*/
    @FXML private Button sExitBtn;
    /** Button for creating an exit to the west of selected room.*/
    @FXML private Button wExitBtn;
    /** Button to open a window for editing the existing selected room.*/
    @FXML private Button editRoomBtn;
    @FXML private Text selectedRoomTxt;
    /** Combo box for setting the player's starting location.*/
    @FXML private ComboBox<Room> startRoomCbx;
    /** Combo box for selecting the room the player must reach to win game.*/
    @FXML private ComboBox<Room> winRoomCbx;
    /** Vbox for holding all items the player starts with.*/
    @FXML private VBox inventoryVbox;
    /** Button for exiting the generator without saving the game.*/
    @FXML private Button exitNoSaveBtn;

    /** Initialises the window, configuring the interface.*/
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

    /** Initialises a new RoomConfigController window for creating/editing a room.
     * If MouseEvent is the new room button, creates a default window. Otherwise, MouseEvent
     * should be the edit room button and loads the fields of the room into the new window.
     * @throws IOException if window elements cannot be loaded.
     * @param event Determines what button has been clicked.*/
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

    /** Deletes the currently selected room, first displaying a confirmation message.
     * Currently deletes all entities inside the room when deleted.
     * @throws InvalidGameConfigError if user is trying to delete the last room in the game.*/
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

    /** Initialises a new ItemConfigController window for creating/editing an item.
     * If MouseEvent is the new item button, creates a default window. Otherwise, MouseEvent
     * should be an existing item from the tree and loads the fields of the item into the new window.
     * @throws IOException if window elements cannot be loaded.
     * @param event Determines what button has been clicked.*/
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

    /** Initialises a new EnemyConfigController window for creating/editing an enemy.
     * If MouseEvent is the new enemy button, creates a default window. Otherwise, MouseEvent
     * should be an existing enemy in the entity tree and loads the fields of the enemy into the new window.
     * @throws IOException if window elements cannot be loaded.
     * @param event Determines what button has been clicked.*/
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

    /** Initialises a new ExitConfigController window for editing room connections from the
     * currently selected room. MouseEvent should be one of four values; there are corresponding
     * buttons for north, east, west and south.
     * @throws IOException if window elements cannot be loaded.
     * @param event Determines what button has been clicked.*/
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

    /** Updates the interface to show attributes of a given room when clicked in the entity tree.
     * @param event Determines what room has been selected from the entity tree.*/
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

    /** Populates the entity tree and is called whenever an entity is added, edited or removed.*/
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

    /** Updates the list values in the room combo boxes to reflect all rooms in the game.*/
    @FXML
    private void populateRoomCombos() {
        //updates the room combo boxes to show all rooms in game map
        startRoomCbx.getItems().setAll(newGame.getGameMap());
        startRoomCbx.setValue(newGame.getStartingRoom());

        winRoomCbx.getItems().setAll(newGame.getGameMap());
        winRoomCbx.setValue(newGame.getWinCondition());
    }

    /** The main bulk of entity tree generation. Loops through all game rooms and creates
     * a button for each entity, adding the entity ID to the button ID for loading entities
     * in their respective ConfigController. Performs recursive calls to search for nested
     * items and display them indented relative to their level of nesting.*/
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

    /** The recursive function called in generateRoomItems(). Searches for nested items
     * to generate buttons with IDs corresponding to the item's unique ID.
     * @param entityList The collection of entities to be searched, e.g. a container's held items.
     * @param depth The current nested depth, determines the offset of this item in display.*/
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

    /** Method for configuring button appearance when generating in the entity tree.
     * Sets layout, style class, offset. Adds button to the anchor pane for display.
     * @param btn The button to configure.
     * @param xoffset The offset that the button should be placed at determined by recursion depth of caller.*/
    public void configBtn(Button btn, int xoffset) {
        //helper method for configuring appearance/placement of buttons generated in generateRoomItems~related methods
        btn.setLayoutY(spOffset);
        btn.setLayoutX(xoffset);
        btn.getStyleClass().add("buttonscroll");
        spOffset = spOffset + ENTITY_INCREMENTAL_OFFSET;
        objectAnchorPane.getChildren().add(btn);
    }

    /** Creates button nodes for all items in the player's starting inventory.*/
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

    /** Simulates a user button click for certain cases where we want the interface
     * to update naturally as it would when the user interacts with it.
     * @param but The button to simulate a click on. Calls this button's event.*/
    @FXML
    private void simulateButtonClick(Button but) {
        //simulate button click for button arg
        but.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, but.getLayoutX(), but.getLayoutY(), but.getLayoutX(), but.getLayoutY(),
                MouseButton.PRIMARY, 1, true, true, true, true, true,
                true, true, true, true, true, null));
    }

    /** Passes a request to the game manager to try and save the configured game before exiting generator.
     * @throws IOException if main menu window elements cannot be loaded.
     * @param event Determines what button has been clicked.*/
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

    /** Closes the generator and reopens the main menu.
     * @throws IOException if main menu window elements cannot be loaded.*/
    private void quit() throws IOException {
        //returns to the main menu by loading fxml/setting scene
        String fxml = "sample.fxml";

        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stage = (Stage) exitNoSaveBtn.getScene().getWindow();
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    /** Called when user tries to exit without saving. Produces a confirmation prompt to ensure
     * this is what the user wants to do.
     * @throws IOException if main menu window elements cannot be loaded.*/
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

    /** Loads a pre-existing game configuration into the generator, populating all
     * nodes to display the correct information.
     * @throws IOException if window elements cannot be loaded.*/
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

    /** Updates the game's title and player name with the input values when game is saved,
     * as they are input through text fields.*/
    @FXML
    private void setGameParams() {
        //update generic game parameters on ui
        newGame.setTitle(nameEntryTF.getText());
        newGame.getPlayer().setName(playerEntryTF.getText());
    }

    /** Calls all interface element methods that may be updated upon completion of an action.*/
    public void updateInterfaceDisplay() {
        //update ui when new data is added
        populateScrollPane(); //updates scrollpane to show any new entities added/deleted
        populateRoomCombos(); //update combobox list of valid starting rooms
        populateInventoryItems(); //update user's starting items display
    }

    /** For passing the game reference to newly generated windows, providing a constant reference.
     * @return Game The game file being configured.*/
    public static Game getNewGame() { return newGame; }

    /** Updates the room display to show a particular room. Called when a room is deleted
     * and was previously selected on the room display.
     * @param string The room to update the display with.*/
    public void newRoomDisplay(String string) {
        //simulate button click to update ui, call from external window
        Button button = (Button) objectAnchorPane.lookup("#" +string);
        simulateButtonClick(button);
    }

    /** Checks that all game input parameters are valid before the game can be saved.*/
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
