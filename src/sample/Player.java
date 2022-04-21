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

    public String acquire(String item) { //attempt to add item to player inventory
        for (Item i : this.getCurrentRoom().getItems()) {
            if (i.canBeTaken() && i.getName().equalsIgnoreCase(item)) { //find item match and ensure is carryable
                this.getCurrentRoom().deleteItem(i); //remove this item from room
                this.incrementTurnCount(); //successful action, increment player turn count
                return this.getInventory().addItem(i); //add item to player inventory
            }
        }
        return "You could not get that item.";
    }

    public String drop(String item) { //attempt to remove item from player inventory
        for (Item i : this.getInventory().getContents()) {
            if (i.getName().equalsIgnoreCase(item)) {
                this.getInventory().removeItem(i); //remove item from player inventory
                this.getCurrentRoom().addItem(i); //add item to player's current room
                incrementTurnCount();
                return "Dropped " + i.getName() + ".";
            }
        }
        return "You are not holding that item.";
    }

    public void give(Item item) { //give player a new item
        this.getInventory().addItem(item);
        System.out.println("Gave player " + item);
    }

    public String travel(Direction dir) {
        //transfer player to room at indicated direction from current room, if possible
        if (this.getCurrentRoom().checkForExit(dir)) { //ensure there is an exit in this direction
            Room r = this.getCurrentRoom().getExit(dir);
            String output;

            if (!this.getCurrentRoom().isDirectionBlocked(dir)) {
                this.setCurrentRoom(r); //direction is not blocked, transfer player
                output = "You are in: " + r.getName() + "\n" +r.getDescription();
            } else {
                output = r.getLockedText(dir); //direction is blocked, return the associated locked text
            }
            return output;
        }
        return "There is no exit to the " + dir;
    }

    public String viewSelf() { //return information about the player
        int inventorySize = this.getInventory().countItems();
        String item = "items";
        if (inventorySize == 1)
            item = "item";
        return "YOUR NAME IS " + this.getName() + ".\nCurrent HP: " + this.getHp() + "/" + this.getMaxHp() +
                "\nYou are carrying " + this.getInventory().countItems() + " " + item + ".";
    }

    public String getBearings() { //display limited information about player's current room
        return "YOU STAND IN: " + this.getCurrentRoom().getName() + "\n" +
                this.getCurrentRoom().getDescription() +"\nThere are exits to the:" +
                this.getCurrentRoom().listAvailableDirections() + "\n" + checkSurroundings();
    }

    public String checkSurroundings() { //display more information about player's current room
        String desc = this.getCurrentRoom().getDescription();
        List<Item> items = this.getCurrentRoom().getVisibleItems();
        ArrayList<Enemy> enemies = this.getCurrentRoom().getLivingEnemies();
        String itemsSeen = "You see... ";

        if (items.size() == 0) {
            itemsSeen = itemsSeen.concat("nothing."); //no entitites to report to player
        }

        for (int i = 0; i < items.size(); i++) { //display items in current room
            itemsSeen = itemsSeen.concat(items.get(i).getName());
            if (i != (items.size()-1)) {
                itemsSeen = itemsSeen.concat(", ");
            }
            else {
                itemsSeen = itemsSeen.concat(".");
            }
        }

        if (!enemies.isEmpty()) { //display enemies in current room, if any
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

    public String viewItem(String item) { //view item description
        Item i = this.getInventory().findItemByName(item);
        if (i == null) {
            i = this.getCurrentRoom().findItemByName(item);
        }
        if (i == null) {
            return "That item cannot be found.";
        }
        return i.getDetailedDescription(); //get description with more info for engine
    }

    public ArrayList<Item> getInteractables() { //get all items a player can interact with
        ArrayList<Item> interactables = new ArrayList<>(this.getInventory().getContents());
        for (Item i : getCurrentRoom().getItems()) {
            if (!i.getIsCarry()) //player can interact with non carryable items not in the inventory
                interactables.add(i);
        }
        return interactables;
    }
}

