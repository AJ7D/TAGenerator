package sample;

import java.util.HashMap;

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

    @Override
    public String getDescription() {
        return super.getDescription() + "\nMight: " + this.getMight() + "\nRemaining durability: " + this.getDurability();
    }

    @Override
    public String use(Player p, Item item2) {
        //TODO
        return super.use(p, item2);
    }

    public int getMight() {
        return this.might;
    }

    public int getDurability() {
        return this.durability;
    }
}
