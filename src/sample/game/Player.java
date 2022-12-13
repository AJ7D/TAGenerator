package sample.game;

import sample.engine.EngineController;

import java.util.ArrayList;
import java.util.List;

/** Class for defining the player which acts as the user's interaction with games in the game engine.
 * @see Game
 * @see EngineController */
public class Player extends Character {
    /** The number of turns elapsed by the player in a game.*/
    private int turnCount = 0;

    /** Constructor for creating a player with a name.
     * @param n The name to be set to the player.*/
    Player(String n) {
        super(n);
    }

    /** Constructor for creating a player with a name and a current room.
     * @param name The name to be set to the player.
     * @param currentRoom The room to place the player into by default.*/
    Player(String name, Room currentRoom) {
        super(name, currentRoom);
    }

    /** Returns the number of turns elapsed for the player.
     * @return int The number of turns elapsed by the player.*/
    public int getTurnCount() {
        return turnCount;
    }

    /** Increments the player's turn count by 1.*/
    public void incrementTurnCount() {
        turnCount++;
    }

    /** Player tries to get a given item by checking if it exists and can be taken.
     * @param item The item to be added to the player's inventory.
     * @return Describes if the player gets the item or not.*/
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

    /** Player tries to drop a given item by checking if it is in player's inventory.
     * @param item The item to be removed from the player's inventory.
     * @return Describes if the player drops the item or not.*/
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

    /** Gives the player an item directly without relevant output.
     * @param item Item to be given to player.*/
    public void give(Item item) { //give player a new item
        this.getInventory().addItem(item);
        System.out.println("Gave player " + item);
    }

    /** Tries to relocate the player to the room in the given direction from player's current room.
     * Fails if no room in that direction or direction is locked.
     * @param dir Direction to travel to from current room.
     * @return String Describes if the player successfully travelled.
     * @see Room
     * @see Direction*/
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

    /** Displays information about the player, such as name, HP and inventory items.
     * @return String Formatted string of player information.*/
    public String viewSelf() { //return information about the player
        int inventorySize = this.getInventory().countItems();
        String item = "items";
        if (inventorySize == 1)
            item = "item";
        return "YOUR NAME IS " + this.getName() + ".\nCurrent HP: " + this.getCurrentHp() + "/" + this.getMaxHp() +
                "\nYou are carrying " + this.getInventory().countItems() + " " + item + ".";
    }

    /** Displays information about the player's current room.
     * @return String Formatted string of current room information.
     * @see Room*/
    public String getBearings() { //display limited information about player's current room
        return "YOU STAND IN: " + this.getCurrentRoom().getName() + "\n" +
                this.getCurrentRoom().getDescription() +"\nThere are exits to the:" +
                this.getCurrentRoom().listAvailableDirections() + "\n" + checkSurroundings();
    }

    /** Displays more elaborate information about the player's current room.
     * @return String Formatted string of current room information.
     * @see Room*/
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

    /** Displays the detailed description of the given item for use in the engine, NOT THE GENERATOR.
     * @param item The item to be viewed.
     * @return String Displays the detailed description of the given item.*/
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

    /** Gets an ArrayList of all items that the player can perform actions on.
     * @return ArrayList The items that the player can interact with.
     * @see Action*/
    public ArrayList<Item> getInteractables() { //get all items a player can interact with
        ArrayList<Item> interactables = new ArrayList<>(this.getInventory().getContents());
        for (Item i : getCurrentRoom().getItems()) {
            if (!i.getIsCarry()) //player can interact with non carryable items not in the inventory
                interactables.add(i);
        }
        return interactables;
    }
}

