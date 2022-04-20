package sample;

public class Enemy extends Character {
    private int attack = 10;
    private EnemyState state = EnemyState.PASSIVE; //indicates if enemy attacks player

    Enemy() {super();}

    Enemy(String n) {
        super(n);
    }

    Enemy(String name, Room currentRoom) {
        super(name, currentRoom);
    }

    Enemy(String name, Room currentRoom, int attack) {
        super(name, currentRoom);
        this.attack = attack;
    }

    Enemy(String name, Room currentRoom, int attack, EnemyState enemyState) {
        super(name, currentRoom);
        this.attack = attack;
        this.state = enemyState;
    }

    Enemy(String name, Room currentRoom, int hp, int attack, EnemyState enemyState) {
        super(name, currentRoom);
        this.setMaxHp(hp);
        this.attack = attack;
        this.state = enemyState;
    }

    public EnemyState getState() {
        return state;
    }

    public String processTurn(Player p) {
        switch (this.state) {
            case AGGRESSIVE:
                if (this.isAlive())
                    return this.attack(p); //attack player
                else
                    return this.dropInventory(); //newly dead, drop inventory
            case PASSIVE:
                if (!this.isAlive())
                    return this.dropInventory();
            default:
                return ""; //no action to be reported at this time
        }
    }

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

    public boolean isAlive() {
        return this.getHp() > 0;
    }

    public String attack(Character character) {
        //remove hp from character proportional to this enemy's attack stat
        character.setHp(character.getHp()-this.getAttack());
        return this.getName() + " attacked " + character.getName() + ", dealing " + this.getAttack() + " damage.";
    }

    public int getAttack() {
        return this.attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

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
        System.out.println(p.getHp());
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
