package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Item> contents = new ArrayList<>();
    private int capacity;

    public static int NO_CAPACITY = -1;

    public Inventory() {
        this.capacity = NO_CAPACITY;
    }

    public Inventory(int capacity) {
        this.capacity = capacity;
    }

    public Inventory(List<Item> contents) {
        this.contents = contents;
    }

    public List<Item> getContents() { return this.contents; }

    public void addItem(Item itemToAdd) {
        if (this.getContents().size() == this.capacity) {
            System.out.println("You are carrying too many items.");
            return;
        }
        if (contents != null) {
            for (Item i : contents) {
                if (i.getId() == itemToAdd.getId())
                    //increment count
                    return;
            }
        }
        contents.add(itemToAdd);
    }

    public void removeItem(Item itemToRemove) {
        if (contents != null) {
            for (Item i : contents) {
                if (i.getId() == itemToRemove.getId()) {
                    contents.remove(itemToRemove);
                    return;
                }
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

    public void viewItems() {
        System.out.println("YOUR INVENTORY CONTAINS: ");
        if (contents.size() == 0) {
            System.out.println("Nothing!");
            return;
        }
        for (Item i : contents) {
            System.out.println(i.getName());
        }
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
