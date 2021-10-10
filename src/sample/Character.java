package sample;

import java.io.Serializable;

public class Character implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private final Inventory inventory = new Inventory();
    private Room currentRoom;

    Character(String n) {
        this.name = n;
    }

    Character(String n, Room cr) {
        this.name = n;
        this.currentRoom = cr;
    }

    public String getName() { return this.name; }

    public void setName(String newName) { this.name = newName; }

    public Inventory getInventory() { return this.inventory; }

    public Room getCurrentRoom() { return this.currentRoom; }

    public void setCurrentRoom(Room cr) { this.currentRoom = cr; }

    public String checkInventory() {
        return this.getInventory().viewItems();
    }
}

