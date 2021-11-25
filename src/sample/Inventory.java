package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Item> contents = new ArrayList<>();
    private int capacity;

    public static int NO_CAPACITY = Integer.MAX_VALUE;

    public Inventory() {
        this.capacity = NO_CAPACITY;
    }

    public Inventory(int capacity) {
        this.capacity = capacity;
    }

    public List<Item> getContents() { return this.contents; }

    public int getCapacity() { return this.capacity; }

    public void setCapacity(int newCapacity) { this.capacity = newCapacity; }

    public String addItem(Item itemToAdd) {
        if (this.getContents().size() == this.capacity) {
            return "You are carrying too many items.";
        }
        contents.add(itemToAdd);
        return "Picked up " + itemToAdd.getName() + ".";
    }

    public void removeItem(Item itemToRemove) {
        for (Item i : contents) {
            if (i.getId() == itemToRemove.getId()) {
                contents.remove(itemToRemove);
                return;
            }
        }
        System.out.println(itemToRemove + " was not found.");
    }

    public String viewItem(Item itemToView) {
        for (Item i : contents) {
            if (i.getId() == itemToView.getId())
                return i.getDescription();
        }
        return "Item not found.";
    }

    public Item findItemByName(String item) {
        for (Item i : contents) {
            if (i.getName().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return null;
    }

    public String viewItems() {
        String output = "";
        output = output.concat("YOUR INVENTORY CONTAINS:\n");
        if (contents.size() == 0) {
            output = output.concat("Nothing!");
        }
        else {
            for (Item i : contents) {
                output = output.concat(i.getName() + "\n");
            }
        }
        return output;
    }

    public int countItems() {
        return contents.size();
    }

    public boolean containsItem(Item item) {
        for (Item i : this.contents) {
            if (i.compareItem(item)) {
                return true;
            }
        }
        return false;
    }

}
