package sample;

import java.util.HashMap;

public class Key extends Item{
    private HashMap<Long, Container> compatibility;

    Key() {
        super();
        this.compatibility = new HashMap<>();
    }

    Key(String name, String description, boolean isVisible, boolean isCarry, boolean startWith) {
        super(name, description, isVisible, isCarry, startWith);
        this.compatibility = new HashMap<>();
    }

    Key(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, HashMap<Long, Container> comp) {
        super(name, description, isVisible, isCarry, startWith);
        this.compatibility = comp;
    }

    @Override
    public String use(Player p, Item item2) {
        boolean valid = this.compatibility.containsKey(item2.id);
        if (valid) {
            if (item2 instanceof Container) {
                if (((Container) item2).getLockState() == LockState.LOCKED) {
                    ((Container) item2).setLockState(LockState.UNLOCKED);
                    return item2.getName() + " has been unlocked. ";
                }
                else if (((Container) item2).getLockState() == LockState.UNLOCKED) {
                    ((Container) item2).setLockState(LockState.LOCKED);
                    return item2.getName() + " has been locked. ";
                }
                else {
                    return item2.getName() + " can no longer be opened.";
                }
            }
        }
        return this.getName() + " is ineffective on " + item2.getName(); //TODO
    }

    public void addCompatibility(Container container) {
        this.compatibility.put(container.getId(), container);
    }

    public void removeCompatibility(Container container) {
        this.compatibility.remove(container.getId());
    }
}
