package sample.game;

import java.util.ArrayList;

/**
 * Container class that extends Item with additional functionality for items that hold other items.
 * @see Item
 */
public class Container extends Item {
    /** The items held by the container.*/
    private ArrayList<Item> items = new ArrayList<>();
    /** The lock state determines if the container can be accessed by a character.
     * @see LockState*/
    private LockState lockState = LockState.LOCKED;

    /** Default constructor for a container.*/
    public Container() {
        super();
    }

    /** Constructor for a container providing all basic parameters.
     * @param name The name of the container.
     * @param description The description of the container.
     * @param isVisible Determines if players can see the container.
     * @param isCarry Determines if container can be added to an inventory.
     * @param startWith Determines if this container starts in the player's inventory.*/
    public Container(String name, String description, boolean isVisible, boolean isCarry, boolean startWith) {
        super(name, description, isVisible, isCarry, startWith);
    }

    /** Constructor for a container providing all parameters, including predefined holding items.
     * @param items The items that should be inside the container.*/
    public Container(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, ArrayList<Item> items) {
        super(name, description, isVisible, isCarry, startWith);
        this.items = items;
    }

    /** Constructor for a container providing all parameters, including predefined lock state.
     * @param locked The lock state of the container.
     * @see LockState*/
    public Container(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, ArrayList<Item> items, LockState locked) {
        super(name, description, isVisible, isCarry, startWith);
        this.items = items;
        this.lockState = locked;
    }

    /** Constructor for a container providing all parameters, including a specific ID for overwriting an existing item.
     * @param id The ID of the container, for overwriting existing items.*/
    public Container(Long id, String name, String description, boolean isVisible, boolean isCarry, boolean startWith, ArrayList<Item> items, LockState locked) {
        super(id, name, description, isVisible, isCarry, startWith);
        this.items = items;
        this.lockState = locked;
    }

    /** Constructor for a container providing all basic parameters, as well as lock state.
     * @see LockState */
    public Container(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, LockState locked) {
        super(name, description, isVisible, isCarry, startWith);
        this.lockState = locked;
    }

    /** Gets a more detailed description of the container than the custom description
     * input by the game creator. Includes details on items contained if not locked.
     * @return String A string of the container's description and items contained, if not locked.*/
    public String getDetailedDescription() {
        //takes player's custom description and adds additional information
        //about the weapon's stats for output in the game engine
        String desc = super.getDescription() + "\n";
        switch(lockState) {
            case LOCKED: //locked, cannot display item contents
                return desc + this.getName() + " is locked.";
            case UNLOCKED: //unlocked, display item contents if any
                String contents = "";
                if (items.isEmpty()) {
                    contents = "Nothing.";
                }
                else {
                    for (Item i : this.items) {
                        contents = contents.concat(i.getName() + "\n");
                    }
                }
                return desc + "Contents of " + this.getName() +":\n" + contents;
            default: //if not LOCKED or UNLOCKED, container is JAMMED, report to player
                return desc + this.getName() + " can no longer be opened.";
        }
    }

    /** Overrides default item use method for using a container. Containers cannot be used
     * without supplying an item to take/place, therefore this returns a generic string message.
     * @param p The player to use the container.
     * @return String A string indicating that the container must be used with an item.*/
    @Override
    public String use(Player p) { //containers must be used with another item
        return "What are you trying to do with " + this.getName() + "?";
    }

    /** Overrides default item use method for using a consumable with an item.
     * Takes the item from the container if item is contained in container, otherwise
     * checks if player has the item to place in the container.
     * @param p The player to use the container.
     * @param item2 The item to use with the container.
     * @return String A string indicating if the action was successful and the effects.*/
    @Override
    public String use(Player p, Item item2) {
        switch (lockState) {
            case LOCKED: {
                if (item2.compatibleWithItem() && p.getInventory().containsItem(item2))
                    return this.getName() + " is locked.\n" + item2.use(p, this);
                return this.getName() + " is locked.";
            }
            case UNLOCKED: {
                if (this.items.contains(item2)) {
                    //player takes item from container
                    p.give(item2);
                    this.items.remove(item2);
                    return item2.getName() + " was taken from " + this.getName();
                } else if (p.getInventory().containsItem(item2) && !(item2.equals(this))) {
                    //player places item into container
                    p.getInventory().removeItem(item2);
                    this.items.add(item2);
                    return item2.getName() + " was placed inside " + this.getName();
                }
                return "What item are you talking about?";
            }
            default: { //container is JAMMED
                return this.getName() + " is locked and can no longer be opened.";
            }
        }
    }

    /** Displays the container's current lock state.
     * @return LockState The current lock state.
     * @see LockState*/
    public LockState getLockState() {
        return lockState;
    }

    /** Sets the container's current lock state.
     * @param lockState The lock state to apply to the container.
     * @see LockState*/
    public void setLockState(LockState lockState) {
        this.lockState = lockState;
    }

    /** Sets the container's contained items.
     * @param items An ArrayList of items for the container to hold.
     * @see Item*/
    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    /** Adds an item to the container's items.
     * @param item The item to add to the container's items.
     * @see Item*/
    public void addItem(Item item) { this.items.add(item); }

    /** Gets the item contents of the container.
     * @return ArrayList The items contained in the container.*/
    public ArrayList<Item> getItems() {
        return items;
    }

    /** Displays the container's items as a string of item names.
     * @return ArrayList An arraylist containing item names in the container.
     * @see Item*/
    public ArrayList<String> getItemNames() {
        ArrayList<String> itemNames = new ArrayList<>();
        for (Item i : items)
            itemNames.add(i.getName());
        return itemNames;
    }

    /** Determines if the item requires another entity to use.
     * @return boolean Returns true as container needs an item to place/remove. */
    @Override
    public boolean compatibleWithItem() {
        return true;
    }

    /** Returns the held items of the container.
     * @return ArrayList Returns items held. */
    @Override
    public ArrayList<? extends Entity> getHeldItems() {
        return items;
    }

    /** Displays the container's information as a string.*/
    @Override
    public String toString() {
        return super.toString() +
                "items=" + getItemNames() +
                ", lockState=" + lockState +
                '}';
    }

    public static void main(String[] args) {
        Player p = new Player("June");
        Container box = new Container();
        ArrayList<Item> compat = new ArrayList<>();
        compat.add(box);
        Key key = new Key("Key", "A key.", true, true, true, compat);
        System.out.println(box.getDescription());
        System.out.println(key.use(p, box));
        System.out.println(box.getDescription());
        System.out.println(key.use(p, box));
        System.out.println(box.getDescription());
        box.setLockState(LockState.JAMMED);
        System.out.println(box.getDescription());
        System.out.println(key.use(p, box));

        box.setLockState(LockState.UNLOCKED);
        Consumable peach = new Consumable("Peach", "Peachy.", true, true, true, 20, 2);
        p.give(peach);
        System.out.println(box.use(p, peach));
        System.out.println(key.use(p, box));
        System.out.println(box.getDescription());
        System.out.println(key.use(p, box));
        System.out.println(box.getDescription());
        System.out.println(box.use(p, peach));
    }
}
