package sample;

public class Consumable extends Item{
    private int hpRestore = 30;
    private int numUses = 1;

    Consumable() {
        super();
    }

    Consumable(String name, String description, Type type, boolean isVisible, boolean isCarry, boolean startWith, int res, int numUses) {
        super(name, description, type, isVisible, isCarry, startWith);
        this.hpRestore = res;
        this.numUses = numUses;
    }
    @Override
    public String use(Player p) {
        String feedback = "";
        p.giveHp(this.hpRestore);
        this.numUses--;
        if (this.numUses <= 0) {
            p.getInventory().removeItem(this);
            feedback = feedback.concat("\nThe " + this.getName() + " was no more.");
        }
        if (this.hpRestore > 0) {
            feedback = "Eating the " + this.getName() + " has restored " + this.hpRestore + " health." + feedback;
        }
        else if (this.hpRestore == 0) {
            feedback = "Eating the " + this.getName() + " has restored no health." + feedback;
        }
        else {
            feedback = "Eating the " + this.getName() + " has dealt " + this.hpRestore + " damage." + feedback;
        }
        return feedback;
    }

    public static void main(String[] args) {
        Player p = new Player("June");
        p.setHp(10);
        Consumable peach = new Consumable("Peach", "A juicy peach.", Type.DEFAULT, true, true, true, 30, 2);
        p.give(peach);
        System.out.println(p.checkInventory());
        System.out.println(p.getHp());
        System.out.println(p.getInventory().findItemByName("Peach").use(p));
        System.out.println(p.getHp());
        System.out.println(p.getInventory().findItemByName("Peach").use(p));
        System.out.println(p.getHp());
        System.out.println(p.checkInventory());
    }
}
