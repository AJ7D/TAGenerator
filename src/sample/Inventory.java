package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Inventory class for maintaining the items of an entity.
 * @serial */
public class Inventory implements Serializable {
    /** Serial version UID for the inventory.*/
    private static final long serialVersionUID = 1L;

    /** Items contained in the inventory.*/
    private final List<Item> contents = new ArrayList<>();
    /** Maximum number of items that can be held by the inventory.*/
    private int capacity;

    /** Default value if no capacity is set.*/
    public static int NO_CAPACITY = Integer.MAX_VALUE;

    /** Default constructor for inventory, sets max capacity to default value (maximum).*/
    public Inventory() {
        this.capacity = NO_CAPACITY;
    }

    /** Constructor for inventory that sets maximum number of items that can be held.
     * @param capacity The maximum capacity for the inventory.*/
    public Inventory(int capacity) {
        this.capacity = capacity;
    }

    /** Gets the items held by the inventory.
     * @return List The list of items held by the inventory.*/
    public List<Item> getContents() { return this.contents; }

    public int getCapacity() { return this.capacity; }

    public void setCapacity(int newCapacity) { this.capacity = newCapacity; }

    /** Adds an item to the inventory's items.
     * @param itemToAdd The item to be added to the inventory.
     * @return String Returns a string indicating if adding the item was successful.*/
    public String addItem(Item itemToAdd) {
        if (this.getContents().size() == this.capacity) {
            return "You are carrying too many items.";
        }
        contents.add(itemToAdd);
        return "Picked up " + itemToAdd.getName() + ".";
    }

    /** Removes an item from the inventory if it exists in the inventory's items.
     * @param itemToRemove The item to be removed from the inventory*/
    public void removeItem(Item itemToRemove) {
        for (Item i : contents) {
            if (i.getId() == itemToRemove.getId()) {
                contents.remove(itemToRemove);
                return;
            }
        }
        System.out.println(itemToRemove + " was not found.");
    }

    /** Finds an item in the inventory by name. Can return null.
     * @param item The item to find.
     * @return Item The item found.*/
    public Item findItemByName(String item) {
        for (Item i : contents) {
            if (i.getName().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return null;
    }

    /** Displays a message of the inventory's contents as a string. Intended for the player.
     * @return String The returned message displaying items in inventory.*/
    public String viewItems() {
        //display all items in this inventory
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

    /** Returns the number of items in the inventory.
     * @return int The number of items in the inventory.*/
    public int countItems() {
        return contents.size();
    }

    /** Checks if the inventory is holding a given item.
     * @param item The item to check for.
     * @return boolean Returns true if item is in inventory.*/
    public boolean containsItem(Item item) {
        for (Item i : this.contents) {
            if (i.compareItem(item)) {
                return true;
            }
        }
        return false;
    }

    /** Gets the inventory contents as a single formatted string.
     * @return String A formatted string listing inventory items by name.
     * @see Item*/
    public String getContentsString() {
        //method for returning all items in inventory as a comma separated string for display purposes
        String s = "";
        for (int i = 0; i < this.getContents().size(); i++) {
            if (i == this.getContents().size()-1) {
                s = s.concat(this.getContents().get(i).getName() + ".");
            }
            else {
                s = s.concat(this.getContents().get(i).getName() + ", ");
            }
        }
        return s;
    }

}
