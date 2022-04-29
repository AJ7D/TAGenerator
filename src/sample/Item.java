package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/** Class for defining items that the player can interact with. Extends entity.
 * @serial
 * @see Entity*/
public class Item extends Entity implements Serializable {
    /** The serial version UID of the item.*/
    private static final long serialVersionUID = 1L;

    /** The item's display name.*/
    private String name;
    /** The item's description.*/
    private String description;
    /** Determines if the item can be seen by the player.*/
    private boolean isVisible;
    /** Determines if the item can be carried by the player.*/
    private boolean isCarry;
    /** Determines if the item starts in the player's inventory.
     * @see Inventory*/
    private boolean startWith;

    /** A list of aliases for the item, distinct from its name.*/
    private ArrayList<String> aliases = new ArrayList<>();
    /** HashMap of verbs that can be applied to the item.
     * @see Action*/
    private HashMap<String, Action> verbs = new HashMap<>();

    /** Default constructor for item, creating a generic object.*/
    Item() {
        this.name = "Perfectly Generic Object";
        this.description = "It's a perfectly generic object.";
        this.isVisible = true;
        this.isCarry = true;
        this.startWith = false;
    }

    /** Constructor for item taking basic parameters.
     * @param name The item's name.
     * @param description The item's description.
     * @param isVisible Determines if the player can see the item.
     * @param isCarry Determines if the player can carry the item.
     * @param startWith Determines if the item starts in the player's inventory.*/
    public Item(String name, String description, boolean isVisible, boolean isCarry, boolean startWith) {
        this.name = name;
        this.description = description;
        this.isVisible = isVisible;
        this.isCarry = isCarry;
        this.startWith = startWith;
    }

    /** Constructor for item taking a unique ID to overwrite an existing item.
     * @param id The unique identifier to be overwritten.
     * @see Entity*/
    public Item(Long id, String name, String description, boolean isVisible, boolean isCarry, boolean startWith) {
        super(id);
        this.name = name;
        this.description = description;
        this.isVisible = isVisible;
        this.isCarry = isCarry;
        this.startWith = startWith;
    }

    /** Gets the item's name.
     * @return String The item's name.*/
    public String getName() {
        return this.name;
    }

    /** Sets the item's name.
     * @param n The new name for the item.*/
    public void setName(String n) { this.name = n; }

    /** Gets the item's description.
     * @return String The item's description.*/
    public String getDescription() {
        return this.description;
    }

    /** Sets the item's description.
     * @param d The new description for the item.*/
    public void setDescription(String d) { this.description = d; }

    /** Gets the detailed description, aka the original description plus additional information
     * about the item. This has no effect for a generic item, but some subclasses require it.
     * @return String The item's detailed description, showing any additional information.*/
    public String getDetailedDescription() { return this.description; }

    /** Determines if the item is visible.
     * @return boolean Returns true if the item is visible.*/
    public boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean v) { this.isVisible = v; }

    /** Determines if the item can be carried.
     * @return boolean Returns true if the item can be carried.*/
    public boolean getIsCarry() { return isCarry; }

    public void setIsCarry(boolean c) { this.isCarry = c; }

    /** Determines if the player starts with the item.
     * @return boolean Returns true if the player starts with the item.*/
    public boolean getStartWith() { return this.startWith; }

    public void setStartWith(boolean b) { this.startWith = b; }

    /** Determines if a character can take the item. Requires item to be visible and carryable.
     * @return boolean Returns true if item can be taken.*/
    public boolean canBeTaken() { return (isVisible && isCarry); }

    /** Determines if two items are the same by comparing their unique ID.
     * @return boolean Returns true if items are the same.*/
    public boolean compareItem(Item item) {
        return item.getId() == this.getId();
    }

    /** Gets the item's applicable verbs.
     * @return HashMap Returns a HashMap of applicable verbs.
     * @see Action*/
    public HashMap<String, Action> getVerbs() {
        return verbs;
    }

    /** Sets the item's applicable verbs given a HashMap of String, Action.
     * @param verbs The HashMap of String, Action to set.
     * @see Action*/
    public void setVerbs(HashMap<String, Action> verbs) {
        this.verbs = verbs;
    }

    /** Returns information about the item's fields as a formatted string.
     * @return String The formatted string containing item fields.*/
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name  +
                ", description='" + description +
                ", isVisible=" + isVisible +
                ", isCarry=" + isCarry +
                ", startWith=" + startWith +
                ", aliases=" + aliases +
                ", verbs=" + verbs.keySet() +
                '}';
    }

    //items of generic type cannot be used
    /** Use functio for the item. Generic items cannot be used, but subclasses can.
     * @param p The player trying to perform the action.
     * @return String The results of trying to use the item.*/
    public String use(Player p) {
        return "You cannot use " + this.name + ".";
    }

    /** Use function for the item with another item. Generic items cannot be used, but subclasses can.
     * @param p The player trying to perform the action.
     * @param item2 The other item to use this item on.
     * @return String The results of trying to use the item.*/
    public String use(Player p, Item item2) {
        return "You cannot use " + this.name + ".";
    }

    /** Use function for the item with another enemy. Generic items cannot be used, but subclasses can.
     * @param p The player trying to perform the action.
     * @param enemy The enemy to use this item on.
     * @return String The results of trying to use the item.*/
    public String use(Player p, Enemy enemy) { return "You cannot do that."; }

    public static void main(String[] args) {
        Item item = new Item();
        Item item2 = new Item();
        System.out.println(item.getId());
        System.out.println(item2.getId());
    }
}

