package sample;

public class Enemy extends Character {
    private int attack = 10;
    private String passiveText = this.getName() + " does not attack.";
    private EnemyState state = EnemyState.PASSIVE;

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

    public String processTurn(Player p) {
        switch (this.state) {
            case AGGRESSIVE:
                if (this.isAlive())
                    return this.attack(p);
                else
                    return this.dropInventory(); //dead
            case PASSIVE:
                if (this.isAlive())
                    return this.passiveText;
                else
                    return this.dropInventory();
            default:
                return "";
        }
    }

    public String dropInventory() {
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
        character.setHp(character.getHp()-this.getAttack());
        return this.getName() + " attacked " + character.getName() + ", dealing " + this.getAttack() + " damage.";
    }

    public int getAttack() {
        return this.attack;
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
