package sample;

import java.util.ArrayList;

/** Key class for defining items that can be used to unlock entities. Extends item.
 * @see Item*/
public class Key extends Item{
    /** ArrayList of items that key is compatible with.
     * @see Container*/
    private ArrayList<Item> compatibility;

    /** Default constructor for key.*/
    Key() {
        super();
        this.compatibility = new ArrayList<>();
    }

    /** Constructor for key taking default parameters.
     * @param name The name of the key.
     * @param description The description of the key.
     * @param isVisible Determines if the key can be seen.
     * @param isCarry Determines if the key can be carried.
     * @param startWith Determines if the player starts with the key.*/
    Key(String name, String description, boolean isVisible, boolean isCarry, boolean startWith) {
        super(name, description, isVisible, isCarry, startWith);
        this.compatibility = new ArrayList<>();
    }

    /** Constructor for key taking default parameters and an initial list of compatibility.
     * @param comp An ArrayList of items to make the key compatible with.*/
    Key(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, ArrayList<Item> comp) {
        super(name, description, isVisible, isCarry, startWith);
        this.compatibility = comp;
    }

    /** Constructor for key taking a unique identifier for overwriting existing items.
     * @param id The unique identifier to overwrite.*/
    Key(Long id, String name, String description, boolean isVisible, boolean isCarry, boolean startWith, ArrayList<Item> comp) {
        super(id, name, description, isVisible, isCarry, startWith);
        this.compatibility = comp;
    }


    /** Overrides the item use function. Keys cannot be used on their own.
     * @param p The player trying to use the key.
     * @return String Describes the results of trying to use the key.
     * @see Item*/
    @Override
    public String use(Player p) { //key items must be used with another entity
        return "What are you trying to do with " + this.getName() + "?";
    }

    /** Overrides the item use with another item function. Tries to unlock the given item with the key.
     * @param p The player trying to use the key.
     * @param item2 The item to try and unlock with the key.
     * @return String Describes the results of trying to use the key on the item.
     * @see Item*/
    @Override
    public String use(Player p, Item item2) {
        boolean valid = this.compatibility.contains(item2); //check if key is compatible with indicated item
        Container container = (Container) item2;
        if (valid) { //key is compatible
            if (container.getLockState() == LockState.LOCKED) {
                container.setLockState(LockState.UNLOCKED); //unlock if item is locked
                return item2.getName() + " has been unlocked. ";
            }
            else if (container.getLockState() == LockState.UNLOCKED) {
                container.setLockState(LockState.LOCKED); //lock if item is unlocked
                return item2.getName() + " has been locked. ";
            }
            else {
                return item2.getName() + " can no longer be opened."; //cannot be locked/unlocked anymore
            }
        }
        return this.getName() + " is ineffective on " + item2.getName();
    }

    /** Returns an ArrayList of Items that the key is compatible with.
     * @return The ArrayList of Items that the key is compatible with.*/
    public ArrayList<Item> getCompatibility() {
        return compatibility;
    }

    public void addCompatibility(Container container) {
        this.compatibility.add(container);
    }

    public void removeCompatibility(Container container) {
        this.compatibility.remove(container);
    }

    /** Determines if the item requires another entity to use.
     * @return boolean Returns true as key needs an entity to open. */
    @Override
    public boolean compatibleWithItem() {
        return true;
    }

    /** Displays information about the key's fields as a formatted string.
     * @return String The formatted string.*/
    @Override
    public String toString() {
        return super.toString() +
                "compatibility=" + compatibility +
                '}';
    }
}
