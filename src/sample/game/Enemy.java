package sample.game;

/** Enemy class for defining game actors that can attack the player, extends Character.
 * @see Character*/
public class Enemy extends Character {
    /** The damage caused to another character when the enemy attacks.*/
    private int attack = 10;
    /** The state of the enemy. Determines if the enemy attacks the player each turn.
     * @see EnemyState*/
    private EnemyState state = EnemyState.PASSIVE; //indicates if enemy attacks player

    /** Default constructor for enemy.
     * @see Character*/
    public Enemy() {super();}

    /** Constructor for an enemy provided with a name.
     * @param n The name of the enemy.*/
    public Enemy(String n) {
        super(n);
    }

    /** Constructor for an enemy provided with a name and a room to be set.
     * @param name The name of the enemy.
     * @param currentRoom The room to place the enemy into.*/
    public Enemy(String name, Room currentRoom) {
        super(name, currentRoom);
    }

    /** Constructor for an enemy provided with a name, room to be set and attack damage.
     * @param attack The attack damage of the enemy.*/
    public Enemy(String name, Room currentRoom, int attack) {
        super(name, currentRoom);
        this.attack = attack;
    }

    /** Constructor for an enemy provided with the current state.
     * @param enemyState The starting state of the enemy.
     * @see EnemyState*/
    public Enemy(String name, Room currentRoom, int attack, EnemyState enemyState) {
        super(name, currentRoom);
        this.attack = attack;
        this.state = enemyState;
    }

    /** Constructor for an enemy provided with the max HP.
     * @param hp The max HP of the enemy.*/
    public Enemy(String name, Room currentRoom, int hp, int attack, EnemyState enemyState) {
        super(name, currentRoom);
        this.setMaxHp(hp);
        this.attack = attack;
        this.state = enemyState;
    }

    /** Displays the enemy's current state..
     * @return EnemyState The current state of the enemy.
     * @see EnemyState*/
    public EnemyState getState() {
        return state;
    }

    /** Determines the enemy's action for a given turn.
     * @param p The player who may be attacked.
     * @return String The string describing the action of the enemy for a turn.*/
    public String processTurn(Player p) {
        switch (this.state) {
            case AGGRESSIVE:
                if (this.isAlive())
                    return this.attackCharacter(p); //attack player
                else
                    return this.dropInventory(); //newly dead, drop inventory
            case PASSIVE:
                if (!this.isAlive())
                    return this.dropInventory();
            default:
                return ""; //no action to be reported at this time
        }
    }

    /** Adds the enemy's inventory to its current room and sets state to DEAD.
     * @return String The string describing the death and drops of the enemy.
     * @see EnemyState
     * @see Inventory*/
    public String dropInventory() {
        //set enemy to dead and drop inventory contents, if any
        this.state = EnemyState.DEAD;
        if (this.getInventory().getContents().isEmpty())
            return this.getName() + " was slain.";
        for (Item i : this.getInventory().getContents()) {
            this.getCurrentRoom().addItem(i);
        }
        return this.getName() + " was slain, dropping " + this.getInventory().getContentsString();
    }

    /** Determines if the enemy is living (HP is above 0).
     * @return boolean Returns true if the enemy is alive.*/
    public boolean isAlive() {
        return this.getCurrentHp() > 0;
    }

    /** Makes the enemy attack the provided character.
     * @param character The character to take damage from the enemy attack.
     * @return String The string describing the interaction.*/
    public String attackCharacter(Character character) {
        //remove hp from character proportional to this enemy's attack stat
        character.setCurrentHp(character.getCurrentHp()-this.getAttack());
        return this.getName() + " attacked " + character.getName() + ", dealing " + this.getAttack() + " damage.";
    }

    /** Gets the enemy's attack stat.
     * @return int The attack stat of the enemy.*/
    public int getAttack() {
        return this.attack;
    }

    /** Sets the enemy's attack stat.
     * @param attack The attack stat to be applied to the enemy.*/
    public void setAttack(int attack) {
        this.attack = attack;
    }

    /** Sets the enemy's state.
     * @param state The state to be applied to the enemy.*/
    public void setState(EnemyState state) {
        this.state = state;
    }

    public static void main(String[] args) {
        Room room = new Room();
        Item jewel = new Item("Jewel", "Shiny.", true, true, false);
        Item pendant = new Item("Pendant", "Shiny.", true, true, false);
        Enemy orc = new Enemy("Orc", room, 20, EnemyState.AGGRESSIVE);
        Player p = new Player("June");
        p.setCurrentRoom(room);

        Weapon sword = new Weapon("Sword", "Sharp.", true, true, false, 30, 2);
        p.give(sword);

        System.out.println(orc.getInventory().addItem(jewel));
        System.out.println(orc.getInventory().addItem(pendant));

        System.out.println(orc.processTurn(p));
        System.out.println(p.getCurrentHp());
        System.out.println(sword.use(p, orc));
        System.out.println(orc.processTurn(p));
        System.out.println(sword.use(p, orc));
        System.out.println(orc.processTurn(p));
        System.out.println(sword.use(p, orc));
        System.out.println(p.acquire("Jewel"));
        System.out.println(p.acquire("Pendant"));
        System.out.println(orc.processTurn(p));
    }
}
