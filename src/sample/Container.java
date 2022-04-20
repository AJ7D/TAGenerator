package sample;

import java.util.ArrayList;

public class Container extends Item {
    private ArrayList<Item> items = new ArrayList<>();
    private LockState lockState = LockState.LOCKED;

    Container() {
        super();
    }

    Container(String name, String description, boolean isVisible, boolean isCarry, boolean startWith) {
        super(name, description, isVisible, isCarry, startWith);
    }

    Container(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, ArrayList<Item> items) {
        super(name, description, isVisible, isCarry, startWith);
        this.items = items;
    }

    Container(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, ArrayList<Item> items, LockState locked) {
        super(name, description, isVisible, isCarry, startWith);
        this.items = items;
        this.lockState = locked;
    }

    Container(Long id, String name, String description, boolean isVisible, boolean isCarry, boolean startWith, ArrayList<Item> items, LockState locked) {
        super(id, name, description, isVisible, isCarry, startWith);
        this.items = items;
        this.lockState = locked;
    }

    Container(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, LockState locked) {
        super(name, description, isVisible, isCarry, startWith);
        this.lockState = locked;
    }

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

    @Override
    public String use(Player p) { //containers must be used with another item
        return "What are you trying to do with " + this.getName() + "?";
    }

    @Override
    public String use(Player p, Item item2) {
        switch (lockState) {
            case LOCKED: {
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

    public LockState getLockState() {
        return lockState;
    }

    public void setLockState(LockState lockState) {
        this.lockState = lockState;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) { this.items.add(item); }

    public ArrayList<Item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return super.toString() +
                "items=" + items +
                ", lockState=" + lockState +
                '}';
    }

    public static void main(String[] args) {
        Player p = new Player("June");
        Container box = new Container();
        ArrayList<Container> compat = new ArrayList<>();
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
