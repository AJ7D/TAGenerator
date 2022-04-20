package sample;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ItemConfigController {
    public Pane pane;

    public Text title;
    public Text itemIdTxt;
    public Text itemNameTxt;
    public Text itemDescTxt;
    public TextArea itemDescTA;
    public Button saveItemBtn;
    public TextField nameEntryTF;

    public Item item;

    public GeneratorController generatorController;
    public ComboBox<String> itemTypeCbx;
    public Text itemTypeTxt;
    public CheckBox isVisibleChx;
    public CheckBox isCarryChx;
    public CheckBox startWithChx;
    public VBox paramsVbox;

    public VBox verbsVbox;
    public ComboBox<String> locSelectCbx;
    public HBox locHbox;

    Game game = GeneratorController.getNewGame();

    private final int MAX_STRING_LENGTH = 50;

    @FXML
    private void initialize() {
        locSelectCbx.getSelectionModel().selectFirst();
        updateHolderCbx();
        itemTypeCbx.getSelectionModel().selectFirst();

        isVisibleChx.selectedProperty().addListener((observable, oldValue, newValue) -> {
            //cannot carry or start with item if not visible, update checkbox to prove
            if (!newValue) {
                isCarryChx.setSelected(false);
                startWithChx.setSelected(false);
            }
        });

        isCarryChx.selectedProperty().addListener((observable, oldValue, newValue) -> {
            //cannot start with item if item cannot be carried, update checkbox to prove
            if (!newValue) {
                startWithChx.setSelected(false);
            }
        });
    }

    public void saveItem() {
        try {
            //get item parameters from user interface
            validateInputs();
            String iName = nameEntryTF.getText();
            String iDesc = itemDescTA.getText();
            boolean iVis = isVisibleChx.isSelected();
            boolean iCarry = isCarryChx.isSelected();
            boolean iStart = startWithChx.isSelected();

            if (item != null) { //if editing an item, delete the old version
                game.deleteItem(item);
            }
            item = readType(iName, iDesc, iVis, iCarry, iStart); //returns an item of the indicated item type
            item.setVerbs(getAllVerbs()); //add user's custom verbs to item grammar

            if (iStart) {
                tryGiveCharacterItem(game.getPlayer(), item); //player starts with item
            } else {
                tryPlaceItem(item); //item starts in a room
            }

            game.updateItem(item); //add item to game structure
            System.out.println(game.getGameItems());
            closeWindow(); //close this item config window
        }
        catch (InvalidInputException e) {
            System.out.println(e.toString());
        }
    }

    public void deleteItem() { //remove item from game
        try {
            game.deleteItem(item);
        }
        catch (NullPointerException e) {
            System.out.println("Item was not saved; creation has been cancelled.");
        }
        closeWindow();
    }

    private void closeWindow(){ //close this item window
        Stage stage = (Stage) saveItemBtn.getScene().getWindow();
        generatorController.updateInterfaceDisplay();
        stage.close();
    }
    
    public void loadItem(String str) {
        item = game.getItem(Long.parseLong(str)); //load item from game items
        //populate item fields based on item parameters
        nameEntryTF.setText(item.getName());
        itemDescTA.setText(item.getDescription());

        try {
            itemTypeCbx.setValue(item.getClass().getSimpleName());
            produceAdditionalParams(); //display additional parameter inputs dependent on item type
            loadVerbs(item);
        }
        catch(Exception e) {
            itemTypeCbx.setValue("Default"); //in case of error, set item type to default
            System.out.println("Error: item type could not be correctly determined (" + e  + ")");
        }

        isVisibleChx.setSelected(item.getIsVisible());
        isCarryChx.setSelected(item.getIsCarry());
        startWithChx.setSelected(item.getStartWith());

        if (!item.getStartWith()) { //find item's location
            Entity e = game.findItemInstance(item);
            if (e != null) {
                locSelectCbx.getSelectionModel().select(e.getClass().getSimpleName());
                updateHolderCbx();
                ComboBox<Entity> cbx = (ComboBox<Entity>) locHbox.getChildren().get(0);
                cbx.getSelectionModel().select(e);
            }
            else {
                locSelectCbx.getSelectionModel().selectFirst();
            }
        }
    }

    public Item readType(String iName, String iDesc, boolean iVis, boolean iCarry, boolean iStart) {
        boolean isOverwrite = (item != null); //true if existing item is being edited
        //produces a new item dependent on the subclass of item selected
        if (item instanceof Container && !itemTypeCbx.getValue().equals("Container")) {
            game.emptyContainer((Container) item);
        }
        switch (itemTypeCbx.getValue()) {
            case "Consumable":
                TextField hp = (TextField) paramsVbox.lookup("#cHpField");
                int hpRest = Integer.parseInt(hp.getText());
                TextField cUses = (TextField) paramsVbox.lookup("#numUsesField");
                int cUse = Integer.parseInt(cUses.getText());

                if (isOverwrite)
                    return new Consumable(item.getId(), iName, iDesc, iVis, iCarry, iStart, hpRest, cUse);
                return new Consumable(iName, iDesc, iVis, iCarry, iStart, hpRest, cUse);
            case "Light":
                ComboBox<String> state = (ComboBox<String>) paramsVbox.lookup("#lStateCbx");
                LightState lightState = LightState.valueOf(state.getValue());
                TextField nUses = (TextField) paramsVbox.lookup("#lNumUsesField");
                int nUse = Integer.parseInt(nUses.getText());

                if (isOverwrite)
                    return new Light(item.getId(), iName, iDesc, iVis, iCarry, iStart, lightState, nUse);
                return new Light(iName, iDesc, iVis, iCarry, iStart, lightState, nUse);
            case "Key":
                ComboBox<Container> compList = (ComboBox<Container>) paramsVbox.lookup("#compCbx");
                ArrayList<Container> comp = new ArrayList<>(compList.getItems());
                System.out.println("compatibility = " + comp.toString());

                if (isOverwrite)
                    return new Key(item.getId(), iName, iDesc, iVis, iCarry, iStart, comp);
                return new Key(iName, iDesc, iVis, iCarry, iStart, comp);
            case "Container":
                ComboBox<String> cState = (ComboBox<String>) paramsVbox.lookup("#cStateCbx");
                LockState lockState = LockState.valueOf(cState.getValue());

                if (isOverwrite) {
                    if (item instanceof Container) {
                        return new Container(item.getId(), iName, iDesc, iVis, iCarry, iStart, ((Container) item).getItems(), lockState);
                    }
                    return new Container(item.getId(), iName, iDesc, iVis, iCarry, iStart, new ArrayList<>(), lockState);
                }
                return new Container(iName, iDesc, iVis, iCarry, iStart, new ArrayList<>(), lockState);
            case "Weapon":
                TextField wMight = (TextField) paramsVbox.lookup("#wMightField");
                int might = Integer.parseInt(wMight.getText());
                TextField wNumUses = (TextField) paramsVbox.lookup("#wNumUsesField");
                int wNumUse = Integer.parseInt(wNumUses.getText());

                if (isOverwrite)
                    return new Weapon(item.getId(), iName, iDesc, iVis, iCarry, iStart, might, wNumUse);
                return new Weapon(iName, iDesc, iVis, iCarry, iStart, might, wNumUse);
            default:
                if (isOverwrite)
                    return new Item(item.getId(), iName, iDesc, iVis, iCarry, iStart);
                return new Item(iName, iDesc, iVis, iCarry, iStart);
        }
    }

    public void tryGiveCharacterItem(Character c, Item item) {
        //gives item to player, if permitted
        if (!c.getInventory().containsItem(item)) {
            game.deleteItemInstances(item);
            c.getInventory().addItem(item);
            System.out.println(c.getInventory());
            return;
        }
        System.out.println("Player already has item.");
    }

    public void tryPlaceItem(Item item) {
        game.deleteItemInstances(item);
        ComboBox cbx = (ComboBox) locHbox.getChildren().get(0);
        switch (locSelectCbx.getValue().toString()) {
            case "Room":
                Room r = (Room) cbx.getValue();
                r.addItem(item);
                break;
            case "Enemy":
                Enemy e = (Enemy) cbx.getValue();
                e.getInventory().addItem(item);
                break;
            case "Container":
                Container c = (Container) cbx.getValue();
                c.addItem(item);
                break;
        }
    }

    public void produceAdditionalParams() throws ClassNotFoundException {
        //generates different nodes for user input dependent on the item type
        switch (itemTypeCbx.getValue()) {
            case "Consumable":
                paramsVbox.getChildren().clear();
                Text cHpText = new Text();
                cHpText.setText("HP restore:");
                TextField cHpField = new TextField();
                cHpField.setText("0");
                cHpField.setId("cHpField");

                Text numUses = new Text();
                numUses.setText("Number of uses:");
                TextField numUsesField = new TextField();
                numUsesField.setText("1");
                numUsesField.setId("numUsesField");

                //if item configured already
                if (item instanceof Consumable) {
                    cHpField.setText(String.valueOf(((Consumable) item).getHpRestore()));
                    numUsesField.setText(String.valueOf(((Consumable) item).getNumUses()));
                }

                paramsVbox.getChildren().addAll(cHpText, cHpField, numUses, numUsesField);
                return;
                //hp restore, uses
            case "Light":
                paramsVbox.getChildren().clear();
                Text lStateText = new Text();
                lStateText.setText("Start state:");
                ComboBox<String> lStateCombo = new ComboBox<>();
                ArrayList<String> lightStates = new ArrayList<>();
                for (LightState l : LightState.values()) {
                    lightStates.add(l.name());
                }
                lStateCombo.setItems(FXCollections.observableList(lightStates));
                lStateCombo.setValue(lightStates.get(0));
                lStateCombo.setId("lStateCbx");

                Text lNumUses = new Text();
                lNumUses.setText("Number of uses:");
                TextField lNumUsesField = new TextField();
                lNumUsesField.setText("1");
                lNumUsesField.setId("lNumUsesField");

                //if item configured already
                if (item instanceof Light) {
                    lStateCombo.setValue((((Light) item).getLightState().name()));
                    lNumUsesField.setText(String.valueOf(((Light) item).getNumUses()));
                }

                paramsVbox.getChildren().addAll(lStateText, lStateCombo, lNumUses, lNumUsesField);

                return;
                //starting state, uses
            case "Key":
                paramsVbox.getChildren().clear();
                Text kCompText = new Text();
                kCompText.setText("Compatible items:");

                HBox addHbox = new HBox();
                ComboBox<Item> containers = new ComboBox<>();
                //combobox contains container instances, but only displays container name

                ArrayList<Item> containerList = new ArrayList<>();

                Class cls = Class.forName(Container.class.getName());

                Button addButton = new Button();
                addButton.setText("Add");

                HBox removeHbox = new HBox();
                ComboBox<Item> containersAdded = new ComboBox<>();
                containersAdded.setId("compCbx");
                Button removeButton = new Button();
                removeButton.setText("Remove");

                //if item configured already
                if (item instanceof Key) {
                    ArrayList<Item> addedList = ((Key) item).getCompatibility();
                    containersAdded.setItems(FXCollections.observableList(addedList));
                    containersAdded.getSelectionModel().selectFirst();

                    containerList.addAll(getAllItemsOfType(cls));
                    containerList.removeIf(addedList::contains);
                    containers.setItems(FXCollections.observableList(containerList));
                    containers.getSelectionModel().selectFirst();
                }
                else {
                    if (!getAllItemsOfType(cls).isEmpty()) {
                        containerList.addAll(getAllItemsOfType(cls));
                        containers.setItems(FXCollections.observableList(containerList));
                        containers.setValue(containerList.get(0));
                    }
                }

                addButton.setOnMouseClicked(event -> {
                    try {
                        Item c = containers.getValue();
                        containers.getItems().remove(c);
                        containersAdded.getItems().add(c);
                        containers.getSelectionModel().selectFirst();
                        containersAdded.getSelectionModel().selectFirst();
                    }
                    catch (NullPointerException e) {
                        System.out.println("No container to add.");
                    }
                });

                removeButton.setOnMouseClicked(event -> {
                    try {
                        Item c = containersAdded.getValue();
                        containersAdded.getItems().remove(c);
                        containers.getItems().add(c);
                        containers.getSelectionModel().selectFirst();
                        containersAdded.getSelectionModel().selectFirst();
                    }
                    catch (NullPointerException e) {
                        System.out.println("No container to remove.");
                    }
                });

                UITools uit = new UITools();
                uit.configureCombobox(containers);
                uit.configureCombobox(containersAdded);

                addHbox.getChildren().addAll(containers, addButton);
                removeHbox.getChildren().addAll(containersAdded, removeButton);

                paramsVbox.getChildren().addAll(kCompText, addHbox, removeHbox);

                return;
                //compatible with
            case "Container":
                paramsVbox.getChildren().clear();
                Text cCompText = new Text();
                cCompText.setText("Items contained:");

                Text cStateText = new Text();
                cStateText.setText("Starting state:");
                ComboBox<String> cStateCombo = new ComboBox<>();
                ArrayList<String> lockStates = new ArrayList<>();
                for (LockState l : LockState.values()) {
                    lockStates.add(l.name());
                }
                cStateCombo.setItems(FXCollections.observableList(lockStates));
                cStateCombo.setValue(lockStates.get(0));
                cStateCombo.setId("cStateCbx");

                //TODO
                if (item instanceof Container) {
                    cStateCombo.setValue((((Container) item).getLockState().name()));
                }

                paramsVbox.getChildren().addAll(cCompText, cStateText, cStateCombo);

                return;
                //items, start state
            case "Weapon":
                paramsVbox.getChildren().clear();
                Text wMightText = new Text();
                wMightText.setText("Attack damage:");
                TextField wMightField = new TextField();
                wMightField.setText("10");
                wMightField.setId("wMightField");

                Text wNumUses = new Text();
                wNumUses.setText("Number of uses:");
                TextField wNumUsesField = new TextField();
                wNumUsesField.setText("5");
                wNumUsesField.setId("wNumUsesField");

                //if item configured already
                if (item instanceof Weapon) {
                    wMightField.setText(String.valueOf(((Weapon) item).getMight()));
                    wNumUses.setText(String.valueOf(((Weapon) item).getDurability()));
                }

                paramsVbox.getChildren().addAll(wMightText, wMightField, wNumUses, wNumUsesField);
                return;
                //might, durability
            default:
                paramsVbox.getChildren().clear();
                //none
        }
    }

    public void genNewVerb() {
        HBox nbx = new HBox();
        TextField tf = new TextField();
        ComboBox<String> cbx = new ComboBox<>();
        cbx.setItems(FXCollections.observableArrayList(
                ("Use"),
                ("Take"),
                ("Drop"),
                ("View")));
        cbx.getSelectionModel().selectFirst();

        Button delBtn = new Button();
        delBtn.setText("Delete");
        delBtn.setOnMouseClicked(event -> {
            try {
                removeVerb(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        nbx.getChildren().addAll(tf,cbx,delBtn);
        verbsVbox.getChildren().add(nbx);
    }

    @FXML
    private void removeVerb(MouseEvent event) throws IOException {
        Button btn = (Button) event.getSource();
        verbsVbox.getChildren().remove(btn.getParent());
    }

    private HashMap<String, Action> getAllVerbs() throws InvalidInputException {
        HashMap<String, Action> verbs = new HashMap<>();
        for (Node n : verbsVbox.getChildren()) {
            HBox h = (HBox) n;
            TextField t = (TextField) h.getChildren().get(0);
            String verb = t.getText();
            if (verb.trim().length() == 0 ) {
                throw new InvalidInputException("Please ensure all action verbs are filled/not blank.");
            }
            ComboBox<String> cbx = (ComboBox<String>) h.getChildren().get(1);
            String action = cbx.getValue();
            verbs.put(verb, Action.stringToAction(action));
        }
        return verbs;
    }

    private TextField getVerbTf(int index) {
        HBox h = (HBox) verbsVbox.getChildren().get(index);
        return (TextField) h.getChildren().get(0);
    }

    private ComboBox<String> getActionCbx(int index) {
        HBox h = (HBox) verbsVbox.getChildren().get(index);
        return (ComboBox<String>) h.getChildren().get(1);
    }

    private void loadVerbs(Item item) {
        HashMap<String, Action> verbs = item.getVerbs();

        int i = 0;
        for (HashMap.Entry<String, Action> entry : verbs.entrySet()) {
            genNewVerb();
            getVerbTf(i).setText(entry.getKey());
            getActionCbx(i).setValue(entry.getValue().getClass().getSimpleName());
            i++;
        }
    }

    public ArrayList<Item> getAllItems() {
        ArrayList<Item> items = new ArrayList<>(game.getPlayer().getInventory().getContents());
        items.addAll(game.getGameItems());
        return items;
    }

    public ArrayList<Item> getAllItemsOfType(Class c) {
        ArrayList<Item> ret = new ArrayList<>();

        for (Item i: getAllItems()) {
            if (c.equals(i.getClass())) {
                ret.add(i);
            }
        }
        return ret;
    }

    public void updateHolderCbx() {
        UITools uit = new UITools();
        locHbox.getChildren().clear();
        switch (locSelectCbx.getValue().toString()) {
            case "Room":
                ComboBox<Room> roomCbx = new ComboBox<>();
                roomCbx.getItems().setAll(GeneratorController.getNewGame().getGameMap());
                roomCbx.getSelectionModel().selectFirst(); //add room list to combobox values
                uit.configureComboboxRoom(roomCbx);
                locHbox.getChildren().add(0, roomCbx);
                break;
            case "Enemy":
                ComboBox<Enemy> enemyCbx = new ComboBox<>();
                try {
                    enemyCbx.getItems().setAll(GeneratorController.getNewGame().getGameEnemies());
                    enemyCbx.getSelectionModel().selectFirst();
                }
                catch (NullPointerException e) {
                    System.out.println("No enemies in game to display.");
                }
                uit.configureComboboxEnemy(enemyCbx);
                locHbox.getChildren().add(0, enemyCbx);
                break;
            case "Container":
                ComboBox<Item> contCbx = new ComboBox<>();
                try {
                    contCbx.getItems().setAll(GeneratorController.getNewGame().getContainers());
                    contCbx.getSelectionModel().selectFirst();
                }
                catch (NullPointerException e) {
                    System.out.println("No containers in game to display.");
                }
                if (item != null)
                    contCbx.getItems().remove(item);
                uit.configureCombobox(contCbx);
                locHbox.getChildren().add(0, contCbx);
                break;
        }
        locHbox.getChildren().add(locSelectCbx);
    }

    public void validateInputs() throws InvalidInputException {
        if (nameEntryTF.getText().trim().length() > MAX_STRING_LENGTH ||
                nameEntryTF.getText().trim().length() == 0) {
            throw new InvalidInputException("Please enter a name between 0-50 characters.");
        }

        if (itemDescTA.getText().trim().length() == 0) {
            throw new InvalidInputException("Please enter a description.");
        }

        if (((ComboBox) locHbox.getChildren().get(0)).getValue() == null) {
            throw new InvalidInputException("Please select a valid location.");
        }
    }

    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }

}
