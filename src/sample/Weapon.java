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
    public String use(Player p, Character character) {
        if (character.getHp() > 0) {
            character.setHp(character.getHp() - this.getMight());
            return p.getName() + " attacked " + character.getName() + ", dealing " + this.getMight() + " damage.";
        }
        return character.getName() + " was already slain.";
    }

    public int getMight() {
        return this.might;
    }

    public int getDurability() {
        return this.durability;
    }
}
