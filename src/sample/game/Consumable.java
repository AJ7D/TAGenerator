package sample.game;

/**
 * Consumable class that extends Item with additional functionality for edible items.
 * @see Item
 */
public class Consumable extends Item{
    /** The amount of HP restored/subtracted if consumable is eaten by a character.*/
    private int hpRestore = 30;
    /** The amount of times consumable can be used before it is removed.*/
    private int numUses = 1;

    /** Default constructor for a consumable.*/
    public Consumable() {
        super();
    }

    /** Constructor for a consumable providing all parameters.
     * @param name The name of the consumable.
     * @param description The description of the consumable.
     * @param isVisible Determines if players can see the consumable.
     * @param isCarry Determines if consumable can be added to an inventory.
     * @param startWith Determines if this consumable starts in the player's inventory.
     * @param numUses Number of uses this consumable has before it is removed.
     * @param res Amount of HP restored/subtracted if consumable is eaten by a character.*/
    public Consumable(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, int res, int numUses) {
        super(name, description, isVisible, isCarry, startWith);
        this.hpRestore = res;
        this.numUses = numUses;
    }

    /** Constructor for a consumable providing all parameters with an ID
     * argument for overwriting an existing item.
     * @param id The ID of the consumable, for overwriting existing items.
     * @see Item */
    public Consumable(Long id, String name, String description, boolean isVisible, boolean isCarry, boolean startWith, int res, int numUses) {
        super(id, name, description, isVisible, isCarry, startWith);
        this.hpRestore = res;
        this.numUses = numUses;
    }

    /** Overrides default item use method for using a consumable.
     * Restores HP to the given player and decrements the usage count of the consumable.
     * @param p The player to use the consumable.
     * @return String A string indicating if the action was successful and the effects.*/
    @Override
    public String use(Player p) {
        String feedback = "";
        p.giveHp(this.hpRestore); //restore or remove player health by consumable's hp value
        this.numUses--;
        if (this.numUses <= 0) { //max uses reached, take consumable from player
            p.getInventory().removeItem(this);
            feedback = feedback.concat("\nThe " + this.getName() + " was no more.");
        }
        if (this.hpRestore > 0) { //consumable restores hp, report to player
            feedback = "Eating the " + this.getName() + " has restored " + this.hpRestore + " health." + feedback;
        }
        else if (this.hpRestore == 0) { //consumable is ineffective
            feedback = "Eating the " + this.getName() + " has restored no health." + feedback;
        }
        else { //consumable deals damage to player
            feedback = "Eating the " + this.getName() + " has dealt " + this.hpRestore + " damage." + feedback;
        }
        p.incrementTurnCount(); //successful action increments player's turn count
        return feedback;
    }

    /** Displays the consumable's information as a string.*/
    @Override
    public String toString() {
        return super.toString() +
                "hpRestore=" + hpRestore +
                ", numUses=" + numUses  +
                '}';
    }

    /** Gets the amount of HP restored/subtracted by the consumable.
     * @return int The amount of HP to restore/subtract upon consumption.*/
    public int getHpRestore() {
        return hpRestore;
    }

    /** Default constructor for a character.
     * @return int The remaining amount of times the consumable can be used.*/
    public int getNumUses() {
        return numUses;
    }

    public static void main(String[] args) {
        Player p = new Player("June");
        p.setCurrentHp(10);
        Consumable peach = new Consumable("Peach", "A juicy peach.", true, true, true, 30, 2);
        p.give(peach);
        System.out.println(p.inventoryContentsAsString());
        System.out.println(p.getCurrentHp());
        System.out.println(p.getInventory().findItemByName("Peach").use(p));
        System.out.println(p.getCurrentHp());
        System.out.println(p.getInventory().findItemByName("Peach").use(p));
        System.out.println(p.getCurrentHp());
        System.out.println(p.inventoryContentsAsString());
    }
}
