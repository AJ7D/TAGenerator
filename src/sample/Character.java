package sample;

import java.io.Serializable;

public class Character implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private final Inventory inventory = new Inventory();
    private Room currentRoom;

    private int maxHp = 50;
    private int hp;

    Character(String n) {
        this.name = n;
        this.hp = this.maxHp;
    }

    Character(String n, Room cr) {
        this.name = n;
        this.currentRoom = cr;
        this.hp = this.maxHp;
    }

    Character(String n, Room cr, int h) {
        this.name = n;
        this.currentRoom = cr;
        this.maxHp = h;
        this.hp = this.maxHp;
    }

    Character(String n, int h) {
        this.name = n;
        this.maxHp = h;
        this.hp = this.maxHp;
    }

    public String getName() { return this.name; }

    public void setName(String newName) { this.name = newName; }

    public Inventory getInventory() { return this.inventory; }

    public Room getCurrentRoom() { return this.currentRoom; }

    public void setCurrentRoom(Room cr) { this.currentRoom = cr; }

    public String checkInventory() {
        return this.getInventory().viewItems();
    }

    public void setMaxHp(int mh) {
        this.maxHp = mh;
    }

    public int getMaxHp() {
        return this.maxHp;
    }

    public void setHp(int h) {
        this.hp = Math.min(h, this.maxHp);
    }

    public int getHp() {
        return this.hp;
    }

    public void giveHp(int hpToGive) {
        if (this.hp + hpToGive > this.maxHp) {
            this.hp = maxHp;
        }
        else if (this.hp + hpToGive < 0) {
            this.hp = 0;
        }
        else {
            this.hp = this.hp + hpToGive;
        }
    }
}

