package sample;

/** Weapon class that extends Item with additional functionality for attacking characters.
 * @see Item*/
public class Weapon extends Item {
    /** The amount of damage caused by the weapon when used.*/
    private int might = 5;
    /** The amount of times the weapon can be used before it breaks.*/
    private int durability = Integer.MAX_VALUE;

    /** Default constructor for weapon.
     * @see Item*/
    Weapon() {
        super();
    }

    /** Constructor for a weapon providing all basic parameters.
     * @param name The name of the weapon.
     * @param description The description of the weapon.
     * @param isVisible Determines if players can see the weapon.
     * @param isCarry Determines if weapon can be added to an inventory.
     * @param startWith Determines if this weapon starts in the player's inventory.*/
    Weapon(String name, String description, boolean isVisible, boolean isCarry, boolean startWith) {
        super(name, description, isVisible, isCarry, startWith);
    }

    /** Constructor for a weapon providing all parameters, including might and durability.
     * @param might The amount of damage dealt when the weapon is used on a character.
     * @param durability The amount of times the weapon can be used before it breaks.*/
    Weapon(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, int might, int durability) {
        super(name, description, isVisible, isCarry, startWith);
        this.might = might;
        this.durability = durability;
    }

    /** Constructor for a weapon providing all parameters, including a specific ID for overwriting an existing item.
     * @param id The ID of the weapon, for overwriting existing items.*/
    Weapon(Long id, String name, String description, boolean isVisible, boolean isCarry, boolean startWith, int might, int durability) {
        super(id, name, description, isVisible, isCarry, startWith);
        this.might = might;
        this.durability = durability;
    }

    /** Gets a more detailed description of the weapon than the custom description
     * input by the game creator. Includes details on might and durability.
     * @return String A string of the weapon's description and stats.*/
    @Override
    public String getDetailedDescription() { //get designer description + details about weapon stats
        return super.getDescription() + "\nMight: " + this.getMight() + "\nRemaining durability: " + this.getDurability();
    }

    public void setMight(int might) {
        this.might = might;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    /** Overrides default item use method for using a container. Weapons cannot be used
     * without supplying a character to attack, therefore this returns a generic string message.
     * @param p The player to use the weapon.
     * @return String A string indicating that the weapon must be used on an enemy.*/
    @Override
    public String use(Player p) { //weapon must be used with another entity
        return "What are you trying to attack with " + this.getName() + "?";
    }

    /** Overrides default item use method on enemy for using a weapon. Attacks the enemy
     * if it is present and alive.
     * @param p The player to use the weapon.
     * @param enemy The enemy to be attacked.
     * @return String Describes the effects of the action.*/
    @Override
    public String use(Player p, Enemy enemy) {
        if (enemy.isAlive()) { //enemy can be attacked
            p.incrementTurnCount(); //successful action, increment player turn
            enemy.setHp(enemy.getHp() - this.getMight()); //update enemy hp
            durability--;
            if (enemy.getState() == EnemyState.PASSIVE) { //a passive enemy becomes aggressive when attacks
                enemy.setState(EnemyState.AGGRESSIVE);
            }

            if (durability <= 0) { //max uses reached, take consumable from player
                p.getInventory().removeItem(this);
                return p.getName() + " attacked " + enemy.getName() + ", dealing " + this.getMight() + " damage.\n" +
                        "The " + this.getName() + " was no more.";
            }
            return p.getName() + " attacked " + enemy.getName() + ", dealing " + this.getMight() + " damage.";
        }
        return enemy.getName() + " was already slain.";
    }

    /** Gets the attack damage of the weapon.
     * @return int The attack damage of the weapon.*/
    public int getMight() {
        return this.might;
    }

    /** Gets the amount of times the weapon can be used before it breaks.
     * @return int The amount of times the weapon can be used.*/
    public int getDurability() {
        return this.durability;
    }

    /** Determines if the item requires another entity to use.
     * @return boolean Returns true as weapon needs an entity to attack. */
    @Override
    public boolean compatibleWithEnemy() {
        return true;
    }

    /** Displays the weapon's information as a string.*/
    @Override
    public String toString() {
        return super.toString() +
                "might=" + might +
                ", durability=" + durability +
                '}';
    }
}
