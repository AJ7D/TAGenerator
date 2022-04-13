package sample;

import java.util.ArrayList;
import java.util.List;

public class Player extends Character {
    private int turnCount = 0;

    Player(String n) {
        super(n);
    }

    Player(String name, Room currentRoom) {
        super(name, currentRoom);
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void incrementTurnCount() {
        turnCount++;
    }

    public String acquire(String item) {
        for (Item i : this.getCurrentRoom().getItems()) {
            if (i.canBeTaken() && i.getName().equalsIgnoreCase(item)) {
                this.getCurrentRoom().deleteItem(i);
                this.incrementTurnCount();
                return this.getInventory().addItem(i);
            }
        }
        return "You could not get that item.";
    }

    public String drop(String item) {
        for (Item i : this.getInventory().getContents()) {
            if (i.getName().equalsIgnoreCase(item)) {
                this.getInventory().removeItem(i);
                this.getCurrentRoom().addItem(i);
                incrementTurnCount();
                return "Dropped " + i.getName() + ".";
            }
        }
        return "You are not holding that item.";
    }

    public void give(Item item) {
        this.getInventory().addItem(item);
        System.out.println("Gave player " + item);
    }

    public String travel(Direction dir) {
        if (this.getCurrentRoom().checkForExit(dir)) {
            Room r = this.getCurrentRoom().getExit(dir);
            String output;

            if (!this.getCurrentRoom().isDirectionBlocked(dir)) {
                this.setCurrentRoom(r);
                output = "You are in: " + r.getName() + "\n" +r.getDescription();
            } else {
                output = r.getLockedText(dir);
            }
            return output;
        }
        return "There is no exit to the " + dir;
    }

    public String viewSelf() {
        int inventorySize = this.getInventory().countItems();
        String item = "items";
        if (inventorySize == 1)
            item = "item";
        return "YOUR NAME IS " + this.getName() + ".\nCurrent HP: " + this.getHp() + "/" + this.getMaxHp() +
                "\nYou are carrying " + this.getInventory().countItems() + " " + item + ".";
    }

    public String getBearings() {
        return "YOU STAND IN: " + this.getCurrentRoom().getName() + "\n" +
                this.getCurrentRoom().getDescription() +"\nThere are exits to the:" +
                this.getCurrentRoom().listAvailableDirections() + "\n" + checkSurroundings();
    }

    public String checkSurroundings() {
        String desc = this.getCurrentRoom().getDescription();
        List<Item> items = this.getCurrentRoom().getVisibleItems();
        ArrayList<Enemy> enemies = this.getCurrentRoom().getLivingEnemies();
        String itemsSeen = "You see... ";

        if (items.size() == 0) {
            itemsSeen = itemsSeen.concat("nothing.");
        }

        for (int i = 0; i < items.size(); i++) {
            itemsSeen = itemsSeen.concat(items.get(i).getName());
            if (i != (items.size()-1)) {
                itemsSeen = itemsSeen.concat(", ");
            }
            else {
                itemsSeen = itemsSeen.concat(".");
            }
        }

        if (!enemies.isEmpty()) {
            itemsSeen = itemsSeen.concat("\nThere are enemies about: ");
            for (int i = 0; i < enemies.size(); i++) {
                itemsSeen = itemsSeen.concat(enemies.get(i).getName());
                if (i != (enemies.size() - 1)) {
                    itemsSeen = itemsSeen.concat(", ");
                } else {
                    itemsSeen = itemsSeen.concat(".");
                }
            }
        }
        return itemsSeen;
    }

    public void talk(Npc npc) {
        if (this.getCurrentRoom().getNpcs().contains(npc)) {
            String speech = npc.speak();
            npc.advanceDialogue();
            System.out.println(speech);
        }
        else {
            System.out.println("You are unable to find " + npc.getName() +".");
        }
    }

    public String viewItem(String item) {
        Item i = this.getInventory().findItemByName(item);
        if (i == null) {
            i = this.getCurrentRoom().findItemByName(item);
        }
        if (i == null) {
            return "That item cannot be found.";
        }
        return i.getDescription();
    }

    public boolean allTrue(boolean[] ar) {
        for (boolean b : ar)
            if (!b)
                return false;
        return true;
    }
}

