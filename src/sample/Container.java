package sample;

import java.util.ArrayList;
import java.util.HashMap;

public class Container extends Item {
    private HashMap<Long, Item> items = new HashMap<>();
    private LockState lockState = LockState.LOCKED;

    Container() {
        super();
    }

    Container(String name, String description, boolean isVisible, boolean isCarry, boolean startWith) {
        super(name, description, isVisible, isCarry, startWith);
    }

    Container(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, HashMap<Long, Item> items) {
        super(name, description, isVisible, isCarry, startWith);
        this.items = items;
    }

    Container(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, HashMap<Long, Item> items, LockState locked) {
        super(name, description, isVisible, isCarry, startWith);
        this.items = items;
        this.lockState = locked;
    }

    Container(Long id, String name, String description, boolean isVisible, boolean isCarry, boolean startWith, HashMap<Long, Item> items, LockState locked) {
        super(id, name, description, isVisible, isCarry, startWith);
        this.items = items;
        this.lockState = locked;
    }

    Container(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, LockState locked) {
        super(name, description, isVisible, isCarry, startWith);
        this.lockState = locked;
    }

    public String getDetailedDescription() {
        String desc = super.getDescription() + "\n";
        switch(lockState) {
            case LOCKED:
                return desc + this.getName() + " is locked.";
            case UNLOCKED:
                String contents = "";
                if (items.isEmpty()) {
                    contents = "Nothing.";
                }
                else {
                    for (Long id: items.keySet()) {
                        contents = contents.concat(items.get(id).getName() + "\n");
                    }
                }
                return desc + "Contents of " + this.getName() +":\n" + contents;
            default:
                return desc + this.getName() + " can no longer be opened.";
        }
    }

    @Override
    public String use(Player p, Item item2) {
        switch (lockState) {
            case LOCKED: {
                return this.getName() + " is locked.";
            }
            case UNLOCKED: {
                if (this.items.containsKey(item2.getId())) {
                    p.give(item2);
                    this.items.remove(item2.getId());
                    return item2.getName() + " was taken from " + this.getName();
                } else if (p.getInventory().containsItem(item2)) {
                    p.getInventory().removeItem(item2);
                    this.items.put(item2.getId(), item2);
                    return item2.getName() + " was placed inside " + this.getName();
                }
                return "What item are you talking about?";
            }
            default: {
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

    public void setItems(HashMap<Long, Item> items) {
        this.items = items;
    }

    public HashMap<Long, Item> getItems() {
        return items;
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
