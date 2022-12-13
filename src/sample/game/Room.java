package sample.game;

import sample.generator.IllegalRoomConnection;

import java.io.Serializable;
import java.util.*;

/** Room class for defining environments that hold non-Room entities. Extends entity.
 * @see Entity*/
public class Room extends Entity implements Serializable {
    /** Generic error message if a direction is locked. Intended to be updated by player in later versions.*/
    public static String GENERIC_FAILURE = "You are unable to travel that way currently.";

    /** Name of the room.*/
    private String name;
    /** Description of the room.*/
    private String description;
    /** ArrayList of Items in the room.*/
    private final ArrayList<Item> items = new ArrayList<>();
    /** ArrayList of Characters in the room.*/
    private final ArrayList<Character> npcs = new ArrayList<>();

    /** ArrayList of Enemies in the room.*/
    private ArrayList<Enemy> enemies = new ArrayList<>();

    /** Determines if a direction is locked by index. Indexes from Direction values.
     * @see Direction*/
    private final boolean[] isLocked = new boolean[4];
    /** Custom locked text for each direction.*/
    private final String[] lockedText = new String[4];
    /** Rooms connected to this room, indexed by direction.*/
    private Room[] exits = new Room[4];

    /** Default constructor for room. Called on creation of a new game.*/
    public Room() {
        this.name = "Room1";
        this.description = "An empty room.";
    }

    /** Constructor for room taking a name and description.
     * @param name The name for the created room.
     * @param description The description for the created room.*/
    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /** Constructor for room taking a name and description, and a predefined list of exits.
     * @param exits A list of Rooms for defining the room's exits.*/
    public Room(String name, String description, Room[] exits) {
        this.name = name;
        this.description = description;
        this.exits = exits;
    }

    /** Gets the room's unique identifier.
     * @return long The unique ID of the room.*/
    public long getId() {
        return this.id;
    }

    /** Gets the room's name.
     * @return long The name of the room.*/
    public String getName() {
        return this.name;
    }

    /** Sets the room's name.
     * @param n The new name of the room.*/
    public void setName(String n) { this.name = n; }

    /** Gets the room's description.
     * @return String The description of the room.*/
    public String getDescription() { return this.description; }

    /** Sets the room's description.
     * @param d The new description of the room.*/
    public void setDescription(String d) { this.description = d; }

    /** Gets the room's items.
     * @return ArrayList The items held by the room.*/
    public ArrayList<Item> getItems() { return this.items; }

    public ArrayList<Character> getNpcs() { return npcs; }

    /** Gets a list of all entities within the room, but not player.
     * @return ArrayList The ArrayList of entities in the room.
     * @see Entity*/
    public ArrayList<Entity> getEntities() {
        ArrayList<Entity> entityList = new ArrayList<>(this.enemies);
        entityList.addAll(this.items);
        return entityList;
    }

    public boolean[] getIsLocked() { return this.isLocked; }

    /** Gets all exits of the room by name.
     * @return String[] A list of strings with each exit's name.*/
    public String[] getExitNames() { //return exit names as list of strings
        String[] rooms = new String[4];
        int i = 0;
        for (Room r : this.exits ) {
            if (r != null) {
                rooms[i] = r.getName();
            }
            i++;
        }
        return rooms;
    }

    /** Determine if a direction from the room is locked.
     * @param dir The direction from the room to check.
     * @return boolean Returns true if the direction is locked.*/
    public boolean isDirectionBlocked(Direction dir) { return this.isLocked[dir.getValue()]; }

    /** Lock or unlock a direction of the room.
     * @param bool Determines if the direction is being locked (false) or unlocked (true).
     * @param dir The direction to update.
     * @see Direction*/
    public void setIsLocked(boolean bool, Direction dir) {
        this.isLocked[dir.getValue()] = bool;
    }

    /** Get the locked text for a given direction.
     * @param dir The direction to check.
     * @return String The locked text of the direction.*/
    public String getLockedText(Direction dir) {
        if (this.lockedText[dir.getValue()] != null) {
            return this.lockedText[dir.getValue()];
        }
        return GENERIC_FAILURE; //if no custom locked text has been set, use generic message
    }

    /** Gets the room's unique identifier.
     * @return long The unique ID of the room.*/
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    /** Gets all the living enemies in the room.
     * @return ArrayList All enemies in the room that are alive.
     * @see Enemy
     * @see EnemyState*/
    public ArrayList<Enemy> getLivingEnemies() { //only return enemies where isAlive = true
        ArrayList<Enemy> enemyList = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isAlive())
                enemyList.add(e);
        }
        return enemyList;
    }

    /** Gets all the exits from the room.
     * @return Room[] A list of room references connected to the room.*/
    public Room[] getExits() { return this.exits; }

    /** Gets the room of a specific direction from the room. Can return null.
     * @param dir The direction from this room to retrieve.
     * @return Room The room found at the direction.
     * @see Direction*/
    public Room getExit(Direction dir) { return this.exits[dir.getValue()]; }

    /** Gets a list of all visible items in the room.
     * @return ArrayList The ArrayList of visible items in the room.
     * @see Item*/
    public ArrayList<Item> getVisibleItems() {
        ArrayList<Item> items = new ArrayList<>();
        for (Item i : this.getItems()) {
            if (i.getIsVisible()) {
                items.add(i);
            }
        }
        return items;
    }

    /** Adds the given item to the room's items.
     * @param item The item to add to the room.*/
    public void addItem(Item item) {
        this.getItems().add(item);
    }

    /** Adds the given enemy to the room's enemies.
     * @param enemy The enemy to add to the room.*/
    public void addEnemy(Enemy enemy) { this.getEnemies().add(enemy);}

    /** Removes the given item from the room's items if it exists.
     * @param item The item to delete from the room.*/
    public void deleteItem(Item item) { this.getItems().remove(item); }

    /** Removes the given enemy from the room's enemies if it exists.
     * @param enemy The enemy to remove from the room.*/
    public void deleteEnemy(Enemy enemy) { this.getEnemies().remove(enemy);}

    /** Gets a formatted string of all directions with a room assigned.
     * @return String The formatted string of directions found with rooms.*/
    public String listAvailableDirections() { //given a room, list all directions with rooms set
        String availableDirections = "";
        for (int i = 0; i < 4; i++) {
            if (exits[i]!=null) {
                availableDirections = availableDirections.concat(" " + Direction.values()[i].name());
            }
        }
        return availableDirections;
    }

    /** Check if there is a room assigned to the direction given from this room.
     * @param dir The direction to check for a room, from this room.
     * @return boolean Returns true if there is a room in the given direction.*/
    public boolean checkForExit(Direction dir) { //check that exit is assigned to this room in given direction
        Room r = this.exits[dir.getValue()];
        return r != null;
    }

    /** Deletes the room in the given direction, if it exists.
     * @param dir The direction to delete a room from.
     * @see Direction*/
    public void deleteExit(Direction dir) { //delete room information at given direction from this room
        if (this.checkForExit(dir)) { //ensure a room exists at direction
            Room other = this.exits[dir.getValue()];
            this.exits[dir.getValue()] = null; //remove room reference from exits list
            System.out.println("Connection " + this.getName() + "-" + dir + "->" + other.getName() + " deleted.");
            other.deleteExit(dir.inverseDir()); //remove this reference from other room's exit at inverse direction
        }
        else {
            System.out.println("No more connections left. Finished at " + this.getName());
        }
    }

    /** Formats the room's fields as a String.
     * @return String The formatted string of room information.*/
    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", items=" + this.getItems() +
                ", npcs=" + npcs +
                ", isLocked=" + Arrays.toString(isLocked) +
                ", lockedText=" + Arrays.toString(lockedText) +
                ", exits=" + Arrays.toString(this.getExitNames()) +
                '}';
    }

    /** Adds a given room to this room's given direction.
     * @param dir The direction to add the exit to from this room.
     * @param roomToConnect The room to add to the exit in the given direction.
     * @return boolean Returns true if the exit was updated successfully.
     * @throws IllegalRoomConnection if trying to add a room to itself.*/
    public boolean addExit(Direction dir, Room roomToConnect) throws IllegalRoomConnection {
        if (this.getId() == roomToConnect.getId())
            throw new IllegalRoomConnection("Room cannot be connected to itself.");
        if (this.checkForExit(dir)) {
            this.deleteExit(dir);
        }
        if (roomToConnect.checkForExit(dir.inverseDir())) {
            roomToConnect.deleteExit(dir.inverseDir());
        }

        this.exits[dir.getValue()] = roomToConnect;
        roomToConnect.exits[dir.inverseDir().getValue()] = this;
        return true;
    }

    /** Checks if the room contains a given item.
     * @param item The item to check for.
     * @return boolean Returns true if the item is in this room.*/
    public boolean containsItem(Item item) { //check if room contains given item
        for (Item i : this.items) {
            if (i.compareItem(item)) {
                return true;
            }
        }
        return false;
    }

    /** Checks if the room contains a given enemy.
     * @param enemy The enemy to check for.
     * @return boolean Returns true if the enemy is in this room.*/
    public boolean containsEnemy(Enemy enemy) { //check if room contains given enemy
        for (Enemy e : enemies) {
            if (e.getId() == enemy.getId())
                return true;
        }
        return false;
    }

    /** Checks if the room has any items in it.
     * @return boolean Returns true if the room has items, false if it has 0 items.*/
    public boolean hasItems() {
        return this.items.size() > 0;
    }

    /** Finds an item in the room by name. Can return null.
     * @param item The item to check for.
     * @return Item The item found.*/
    public Item findItemByName(String item) {
        for (Item i : items) {
            if (i.getName().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return null;
    }

    public Item findItemById(Long id) {
        for (Item i : items) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }
}

