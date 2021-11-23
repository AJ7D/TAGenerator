package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    static final AtomicLong NEXT_ID = new AtomicLong(0);
    final long id = NEXT_ID.getAndIncrement();

    private String name;
    private String description;
    private Type type;
    private boolean isVisible;
    private boolean isCarry;
    private boolean startWith;

    private ArrayList<String> aliases = new ArrayList<>();
    private HashMap<Item, ArrayList<String>> itemCompatibility = new HashMap<>();
    //A paired array of items and their applicable actions, events will be determined by
    //parsing the combination of item/application and looked up e.g. eat apple --> EAT_APPLE

    Item() {
        this.name = "Perfectly Generic Object";
        this.description = "It's a perfectly generic object.";
        this.type = Type.DEFAULT;
        this.isVisible = true;
        this.isCarry = true;
        this.startWith = false;
    }

    public Item(String name, String description, Type type, boolean isVisible, boolean isCarry, boolean startWith) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.isVisible = isVisible;
        this.isCarry = isCarry;
        this.startWith = startWith;
    }

    //GETTERS
    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) { this.name = n; }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String d) { this.description = d; }

    public Type getType() {
        return this.type;
    }

    public void setType(Type t) {this.type = t; }

    public boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean v) { this.isVisible = v; }

    public boolean getIsCarry() { return isCarry; }

    public void setIsCarry(boolean c) { this.isCarry = c; }

    public boolean getStartWith() { return this.startWith; }

    public void setStartWith(boolean b) { this.startWith = b; }

    public boolean canBeTaken() { return (isVisible && isCarry); }

    public void addCompatible(Item item, String string) {
        if (this.itemCompatibility.containsKey(item)) {
            for (String s : this.itemCompatibility.get(item)) {
                if (s.equals(string)) {
                    System.out.println(string + " is already defined for " + item.getName());
                    return;
                }
            }
            ArrayList<String> arrayList = this.itemCompatibility.get(item);
            arrayList.add(string);
            this.itemCompatibility.replace(item, arrayList);
            System.out.println(string + " added to commands for " + item.getName());
            System.out.println("(COMMANDS: " + this.itemCompatibility.get(item) + ")");
            return;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(string);
        this.itemCompatibility.put(item, arrayList);
        System.out.println(string + " added to commands for " + item.getName());
    }

    public boolean compareItem(Item item) {
        return item.getId() == this.getId();
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name  +
                ", description='" + description +
                ", type=" + type +
                ", isVisible=" + isVisible +
                ", isCarry=" + isCarry +
                ", startWith=" + startWith +
                ", aliases=" + aliases +
                ", itemCompatibility=" + itemCompatibility +
                '}';
    }

}

