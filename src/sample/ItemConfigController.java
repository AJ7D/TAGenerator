package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
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
        Type iType = readType();
        boolean iVis = isVisibleChx.isSelected();
        boolean iCarry = isCarryChx.isSelected();
        boolean iStart = startWithChx.isSelected();

        if (item == null) {
            item = new Item(iName, iDesc, iType, iVis, iCarry, iStart);
        }
        else {
            item.setName(iName);
            item.setDescription(iDesc);
            item.setType(iType);
            item.setIsVisible(iVis);
            item.setIsCarry(iCarry);
            item.setStartWith(iStart);
        }

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
        itemTypeCbx.setValue(item.getType().getString());
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

    public Type readType() {
        switch (itemTypeCbx.getValue()) {
            case "Consumable":
                return Type.CONSUMABLE;
            case "Key":
                return Type.KEY;
            default:
                return Type.DEFAULT;
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

    public void setGeneratorController(GeneratorController gc) { this.generatorController = gc; }
}
