package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    public ComboBox<String> roomSelCbx;
    public VBox paramsVbox;

    public VBox verbsVbox;

    Game game = GeneratorController.getNewGame();
    public Room oldRoom;

    @FXML
    private void initialize() {
        ArrayList<String> rooms = new ArrayList<>();
        for (Room r : GeneratorController.getNewGame().getGameMap()) {
            rooms.add(r.getName());
        }
        itemTypeCbx.getSelectionModel().selectFirst();
        roomSelCbx.getItems().setAll(rooms);
        roomSelCbx.getSelectionModel().selectFirst();
    }

    public void saveItem() {
        String iName = nameEntryTF.getText();
        String iDesc = itemDescTA.getText();
        //TODO Type iType = readType();
        boolean iVis = isVisibleChx.isSelected();
        boolean iCarry = isCarryChx.isSelected();
        boolean iStart = startWithChx.isSelected();

        item = readType(iName, iDesc, iVis, iCarry, iStart);
        item.setVerbs(getAllVerbs());

        if (iStart) {
            tryGivePlayerItem(item);
        }
        else {
            tryGiveRoomItem(item);
        }

        game.updateItem(item);
        System.out.println(game.getGameItems());
        closeWindow();
    }

    public void deleteItem() {
        game.deleteItem(item);
        closeWindow();
    }

    private void closeWindow(){
        Stage stage = (Stage) saveItemBtn.getScene().getWindow();
        generatorController.callUpdate();
        stage.close();
    }
    
    public void loadItem(String str) {
        item = game.getItem(str);
        nameEntryTF.setText(item.getName());
        itemDescTA.setText(item.getDescription());

        try {
            itemTypeCbx.setValue(item.getClass().getSimpleName());
            produceAdditionalParams();
        }
        catch(Exception e) {
            itemTypeCbx.setValue("Default");
        }

        isVisibleChx.setSelected(item.getIsVisible());
        isCarryChx.setSelected(item.getIsCarry());
        startWithChx.setSelected(item.getStartWith());

        if (!item.getStartWith()) {
            String r = game.findItemLoc(item).getName();
            if (r != null) {
                roomSelCbx.getSelectionModel().select(r);
            }
            else {
                roomSelCbx.getSelectionModel().selectFirst();
            }
            oldRoom = game.getRoom(r);
        }
    }

    public Item readType(String iName, String iDesc, boolean iVis, boolean iCarry, boolean iStart) {
        switch (itemTypeCbx.getValue()) {
            case "Consumable":
                //TODO pass additional parameters for each item type
                TextField hp = (TextField) paramsVbox.lookup("#cHpField");
                int hpRest = Integer.parseInt(hp.getText());
                TextField cUses = (TextField) paramsVbox.lookup("#numUsesField");
                int cUse = Integer.parseInt(cUses.getText());

                return new Consumable(iName, iDesc, iVis, iCarry, iStart, hpRest, cUse);
            case "Light":
                return new Light(iName, iDesc, iVis, iCarry, iStart, LightState.OFF, 2);
            case "Key":
                return new Key(iName, iDesc, iVis, iCarry, iStart, new HashMap<>());
            case "Container":
                return new Container(iName, iDesc, iVis, iCarry, iStart, new HashMap<>(), LockState.LOCKED);
            case "Weapon":
                return new Weapon(iName, iDesc, iVis, iCarry, iStart, 5, 3);
            default:
                return new Item(iName, iDesc, iVis, iCarry, iStart);
        }
    }

    public void tryGivePlayerItem(Item item) {
        Player p = game.getPlayer();
        if (!p.getInventory().containsItem(item)) {
            if (oldRoom != null) {
                oldRoom.deleteItem(item);
            }
            p.give(item);
            System.out.println(p.getInventory());
            return;
        }
        System.out.println("Player already has item.");
    }

    public void tryGiveRoomItem(Item item) {
        Room r = game.getRoom(roomSelCbx.getValue());
        List<Item> inventory = game.getPlayer().getInventory().getContents();
        if (!r.containsItem(item)) {
            if (oldRoom != null) {
                oldRoom.deleteItem(item);
            }
            inventory.remove(item);

            r.addItem(item);
            System.out.println(r);
        }
    }

    public void produceAdditionalParams() {
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

                Text lNumUses = new Text();
                lNumUses.setText("Number of uses:");
                TextField lNumUsesField = new TextField();
                lNumUsesField.setText("1");

                //if item configured already
                if (item instanceof Light) {
                    lStateCombo.setValue(String.valueOf(((Light) item).getLightState().name()));
                    lNumUses.setText(String.valueOf(((Light) item).getNumUses()));
                }

                paramsVbox.getChildren().addAll(lStateText, lStateCombo, lNumUses, lNumUsesField);

                return;
                //starting state, uses
            case "Key":
                paramsVbox.getChildren().clear();
                Text kCompText = new Text();
                kCompText.setText("Compatible items:");

                //TODO configure
                paramsVbox.getChildren().addAll(kCompText);

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

                //TODO
                if (item instanceof Container) {
                    cStateCombo.setValue(String.valueOf(((Container) item).getLockState().name()));
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

                Text wNumUses = new Text();
                wNumUses.setText("Number of uses:");
                TextField wNumUsesField = new TextField();
                wNumUsesField.setText("5");

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
                new String("Use"),
                new String("Take"),
                new String("Drop"),
                new String("View")));
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

    private HashMap<String, Action> getAllVerbs() {
        HashMap<String, Action> verbs = new HashMap<>();
        for (Node n : verbsVbox.getChildren()) {
            HBox h = (HBox) n;
            TextField t = (TextField) h.getChildren().get(0);
            String verb = t.getText();
            ComboBox<String> cbx = (ComboBox<String>) h.getChildren().get(1);
            String action = cbx.getValue();
            verbs.put(verb, Action.stringToAction(action));
        }
        return verbs;
    }

    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }
}
