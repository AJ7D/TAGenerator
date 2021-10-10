package sample;

import java.util.List;

public class Player extends Character {

    Player(String n) {
        super(n);
    }

    Player(String n, Room cr) {
        super(n, cr);
    }

    public void acquire(String item) {
        for (Item i : this.getCurrentRoom().getItems()) {
            if (i.canBeTaken() && i.getName().toLowerCase().equals(item.toLowerCase())) {
                this.getInventory().addItem(i);
                System.out.println("Picked up " + i.getName() + ".");
                this.getCurrentRoom().deleteItem(i);
                return;
            }
        }
        System.out.println("You could not get that item.");
    }

    public void drop(String item) {
        for (Item i : this.getInventory().getContents()) {
            if (i.getName().toLowerCase().equals(item.toLowerCase())) {
                this.getInventory().removeItem(i);
                System.out.println("Dropped " + i.getName() + ".");
                this.getCurrentRoom().addItem(i);
                return;
            }
        }
        System.out.println("You are not holding that item.");
    }

    public void give(Item item) {
        this.getInventory().addItem(item);
        System.out.println("Gave player " + item);
    }

    public void travel(Direction dir) {
        if (this.getCurrentRoom().checkForExit(dir)) {
            Room r = this.getCurrentRoom().getExit(dir);
            int i = dir.getValue();

            if (!this.getCurrentRoom().isDirectionBlocked(dir)) {
                this.setCurrentRoom(r);
                System.out.println("You are in: " + r.getName());
                System.out.println(r.getDescription());
            } else {
                System.out.println(r.getLockedText(dir));
            }
            return;
        }
        System.out.println("There is no exit to the " + dir);
    }

    public void viewSelf() {
        System.out.println("YOUR NAME IS " + this.getName() + ".");
        int inventorySize = this.getInventory().countItems();
        String item = "items";
        if (inventorySize == 1)
            item = "item";
        System.out.println("You are carrying " + this.getInventory().countItems() + " " + item + ".");
    }

    public void getBearings() {
        System.out.println("YOU STAND IN: " + this.getCurrentRoom().getName());
        System.out.println("What will you do?");
    }

    public void checkSurroundings() {
        String desc = this.getCurrentRoom().getDescription();
        List<Item> items = this.getCurrentRoom().getVisibleItems();
        String itemsSeen = "You see... ";
        System.out.println(desc);

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
        System.out.println(itemsSeen);
    }

    public void talk(Npc npc) {
        if (this.getCurrentRoom().getNpcs().contains(npc)) {
            String speech = npc.speak();
            npc.advanceDialogue();
            System.out.println(speech);
        }
        else {
            System.out.println("You are unable to find " + npc.getName());
        }
    }

    public boolean allTrue(boolean[] ar) {
        for (boolean b : ar)
            if (!b)
                return false;
        return true;
    }
}

