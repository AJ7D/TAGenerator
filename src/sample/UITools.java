package sample;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class UITools {

    public void configureCombobox(ComboBox<Item> cbx) {
        //method to cleanly change an item combobox to display item name

        cbx.setConverter(new StringConverter<Item>() {

            @Override
            public String toString(Item cl) {
                return cl.getName();
            }

            @Override
            public Item fromString(String string) {
                return cbx.getItems().stream().filter(ap ->
                        ap.getName().equals(string)).findFirst().orElse(null);
            }
        });
    }

    public void configureComboboxEnemy(ComboBox<Enemy> cbx) {
        //method to cleanly change an item combobox to display item name

        cbx.setConverter(new StringConverter<Enemy>() {

            @Override
            public String toString(Enemy cl) {
                return cl.getName();
            }

            @Override
            public Enemy fromString(String string) {
                return cbx.getItems().stream().filter(ap ->
                        ap.getName().equals(string)).findFirst().orElse(null);
            }
        });
    }


    public void configureComboboxRoom(ComboBox<Room> cbx) {
        //method to cleanly change an item combobox to display item name

        cbx.setConverter(new StringConverter<Room>() {

            @Override
            public String toString(Room cl) {
                return cl.getName();
            }

            @Override
            public Room fromString(String string) {
                return cbx.getItems().stream().filter(ap ->
                        ap.getName().equals(string)).findFirst().orElse(null);
            }
        });
    }
}
