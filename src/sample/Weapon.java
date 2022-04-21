package sample;

public class Weapon extends Item {
    private int might = 5;
    private int durability = Integer.MAX_VALUE;

    Weapon() {
        super();
    }

    Weapon(String name, String description, boolean isVisible, boolean isCarry, boolean startWith) {
        super(name, description, isVisible, isCarry, startWith);
    }

    Weapon(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, int might, int durability) {
        super(name, description, isVisible, isCarry, startWith);
        this.might = might;
        this.durability = durability;
    }

    Weapon(Long id, String name, String description, boolean isVisible, boolean isCarry, boolean startWith, int might, int durability) {
        super(id, name, description, isVisible, isCarry, startWith);
        this.might = might;
        this.durability = durability;
    }

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

    @Override
    public String use(Player p) { //weapon must be used with another entity
        return "What are you trying to attack with " + this.getName() + "?";
    }

    @Override
    public String use(Player p, Enemy enemy) {
        if (enemy.isAlive()) { //enemy can be attacked
            p.incrementTurnCount(); //successful action, increment player turn
            enemy.setHp(enemy.getHp() - this.getMight()); //update enemy hp
            if (enemy.getState() == EnemyState.PASSIVE) { //a passive enemy becomes aggressive when attacks
                enemy.setState(EnemyState.AGGRESSIVE);
            }

            return p.getName() + " attacked " + enemy.getName() + ", dealing " + this.getMight() + " damage.";
        }
        return enemy.getName() + " was already slain.";
    }

    public int getMight() {
        return this.might;
    }

    public int getDurability() {
        return this.durability;
    }

    @Override
    public String toString() {
        return super.toString() +
                "might=" + might +
                ", durability=" + durability +
                '}';
    }
}
