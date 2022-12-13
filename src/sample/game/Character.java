package sample.game;

import java.io.Serializable;

/**
 * Character class for defining game actors, such as the player or an enemy.
 * @serial
 * @see java.io.Serializable
 */
public abstract class Character extends Entity implements Serializable {
    /** The display name of the character.*/
    private String name;
    /** The inventory belonging to the character, for holding items.*/
    private final Inventory inventory = new Inventory();
    /** The current location of the character.*/
    private Room currentRoom;

    /** The maximum amount of health the character can have at any given time.*/
    private int maxHp = 50;
    /** The current health of the character, range 0 to maxHp.*/
    private int hp;

    /** Default constructor for a character.*/
    Character() {
        this.name = "Character";
        this.hp = this.maxHp;
    }

    /** Constructor for a character that takes a name as input.
     * @param n The name of the new character.*/
    Character(String n) {
        this.name = n;
        this.hp = this.maxHp;
    }

    /** Constructor for a character that takes a name and current room as input.
     * @param n The name of the new character.
     * @param cr The room to place the new character into.*/
    Character(String n, Room cr) {
        this.name = n;
        this.currentRoom = cr;
        this.hp = this.maxHp;
    }

    /** Gets the name of the character.
     * @return String The character name as a string.*/
    public String getName() { return this.name; }

    /** Sets the name of the character.
     * @param newName The new name of the character.*/
    public void setName(String newName) { this.name = newName; }

    /** Gets the inventory of the character.
     * @return Inventory The inventory object assigned to this character.
     * @see Inventory*/
    public Inventory getInventory() { return this.inventory; }

    /** Gets the current room of the character.
     * @return Room The character's current room.
     * @see Room*/
    public Room getCurrentRoom() { return this.currentRoom; }

    /** Sets the character's current room.
     * @param cr The room to place the character into.*/
    public void setCurrentRoom(Room cr) { this.currentRoom = cr; }

    /** Gets the contents of the character's inventory as a string.
     * @return String The contents of the character's inventory, formatted as a string.
     * @see Inventory*/
    public String inventoryContentsAsString() {
        return this.getInventory().viewItems();
    }

    /** Updates the maximum HP and current HP of this character by the specified amount.
     * Both maximum HP and current HP are set to the indicated value.
     * @param mh The new amount of HP for the character to have.*/
    public void initialiseHp(int mh) {
        //updates both character's max health and current health to given value
        this.maxHp = mh; this.hp = mh;
    }

    /** Sets the maximum HP of the character.
     * @param mh The new maximum HP for the character.*/
    public void setMaxHp(int mh) {
        this.maxHp = mh;
    }

    /** Gets the maximum HP of the character.
     * @return int The maximum HP of the character.*/
    public int getMaxHp() {
        return this.maxHp;
    }

    /** Sets the character's current HP.
     * @param h The amount of HP for the character to have.*/
    public void setCurrentHp(int h) {
        this.hp = Math.min(h, this.maxHp);
    }

    /** Gets the character's current HP.
     * @return int The current HP of the character.*/
    public int getCurrentHp() {
        return this.hp;
    }

    /** Adds or reduces a fixed amount of HP to/from the character's current HP. If current HP + hpToGive
     * exceeds maximum HP, current HP is capped at maximum HP. Likewise, current HP cannot go below 0.
     * @param hpToGive The amount of HP to add/subtract.*/
    public void giveHp(int hpToGive) {
        if (this.hp + hpToGive > this.maxHp) {
            //hp cannot go above max hp
            this.hp = maxHp;
        }
        else this.hp = Math.max(this.hp + hpToGive, 0); //if hp change is less than 0 set to 0
    }
}

