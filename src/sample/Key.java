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

    Key(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, ArrayList comp) {
        super(name, description, isVisible, isCarry, startWith);
        this.compatibility = comp;
    }

    Key(Long id, String name, String description, boolean isVisible, boolean isCarry, boolean startWith, ArrayList comp) {
        super(id, name, description, isVisible, isCarry, startWith);
        this.compatibility = comp;
    }

    @Override
    public String use(Player p, Item item2) {
        boolean valid = this.compatibility.contains(item2);
        System.out.println(this.compatibility.toString());
        System.out.println(item2.getId());
        Container container = (Container) p.getCurrentRoom().findItemById(item2.getId());
        if (valid) {
            System.out.println("key is valid");
            if (container.getLockState() == LockState.LOCKED) {
                container.setLockState(LockState.UNLOCKED);
                return item2.getName() + " has been unlocked. ";
            }
            else if (container.getLockState() == LockState.UNLOCKED) {
                container.setLockState(LockState.LOCKED);
                return item2.getName() + " has been locked. ";
            }
            else {
                return item2.getName() + " can no longer be opened.";
            }
        }
        return this.getName() + " is ineffective on " + item2.getName(); //TODO
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
