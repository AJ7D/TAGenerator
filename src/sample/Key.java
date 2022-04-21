package sample;

import java.util.ArrayList;

public class Key extends Item{
    private ArrayList<Item> compatibility;

    Key() {
        super();
        this.compatibility = new ArrayList<>();
    }

    Key(String name, String description, boolean isVisible, boolean isCarry, boolean startWith) {
        super(name, description, isVisible, isCarry, startWith);
        this.compatibility = new ArrayList<>();
    }

    Key(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, ArrayList<Item> comp) {
        super(name, description, isVisible, isCarry, startWith);
        this.compatibility = comp;
    }

    Key(Long id, String name, String description, boolean isVisible, boolean isCarry, boolean startWith, ArrayList<Item> comp) {
        super(id, name, description, isVisible, isCarry, startWith);
        this.compatibility = comp;
    }


    @Override
    public String use(Player p) { //key items must be used with another entity
        return "What are you trying to do with " + this.getName() + "?";
    }

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

    public ArrayList<Item> getCompatibility() {
        return compatibility;
    }

    public void addCompatibility(Container container) {
        this.compatibility.add(container);
    }

    public void removeCompatibility(Container container) {
        this.compatibility.remove(container);
    }

    @Override
    public String toString() {
        return super.toString() +
                "compatibility=" + compatibility +
                '}';
    }
}
