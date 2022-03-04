package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Item extends Entity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Type type;
    private boolean isVisible;
    private boolean isCarry;
    private boolean startWith;

    private ArrayList<String> aliases = new ArrayList<>();
    private HashMap<String, Action> verbs = new HashMap<>();

    //A paired array of items and their applicable actions, events will be determined by
    //parsing the combination of item/application and looked up e.g. eat apple --> EAT_APPLE

    Item() {
        this.name = "Perfectly Generic Object";
        this.description = "It's a perfectly generic object.";
        this.isVisible = true;
        this.isCarry = true;
        this.startWith = false;
    }

    public Item(String name, String description, boolean isVisible, boolean isCarry, boolean startWith) {
        this.name = name;
        this.description = description;
        this.isVisible = isVisible;
        this.isCarry = isCarry;
        this.startWith = startWith;
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

    public boolean compareItem(Item item) {
        return item.getId() == this.getId();
    }

    public HashMap<String, Action> getVerbs() {
        return verbs;
    }

    public void setVerbs(HashMap<String, Action> verbs) {
        this.verbs = verbs;
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
                ", verbs=" + verbs.keySet() +
                '}';
    }

    public String use(Player p) {
        return "You cannot use " + this.name + ".";
    }

    public String use(Player p, Item item2) {
        return "You cannot use " + this.name + ".";
    }

    public String use(Player p, Enemy enemy) { return "You cannot do that."; }

    public static void main(String[] args) {
        Item item = new Item();
        Item item2 = new Item();
        System.out.println(item.getId());
        System.out.println(item2.getId());
    }
}

