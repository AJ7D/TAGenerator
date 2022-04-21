package sample;

import java.io.Serializable;
import java.util.*;

public class Room extends Entity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static String GENERIC_FAILURE = "You are unable to travel that way currently.";

    private String name;
    private String description;
    private final ArrayList<Item> items = new ArrayList<>();
    private final ArrayList<Character> npcs = new ArrayList<>();

    private ArrayList<Enemy> enemies = new ArrayList<>();

    private final boolean[] isLocked = new boolean[4];
    private final String[] lockedText = new String[4];
    private Room[] exits = new Room[4];

    public Room() {
        this.name = "Room1";
        this.description = "An empty room.";
    }

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Room(String name, String description, Room[] exits) {
        this.name = name;
        this.description = description;
        this.exits = exits;
    }

    //GETTERS
    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) { this.name = n; }

    public String getDescription() { return this.description; }

    public void setDescription(String d) { this.description = d; }

    public ArrayList<Item> getItems() { return this.items; }

    public ArrayList<Character> getNpcs() { return npcs; }

    public ArrayList<Entity> getEntities() {
        ArrayList<Entity> entityList = new ArrayList<>(this.enemies);
        entityList.addAll(this.items);
        return entityList;
    }

    public boolean[] getIsLocked() { return this.isLocked; }

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

    public boolean isDirectionBlocked(Direction dir) { return this.isLocked[dir.getValue()]; }

    public void setIsLocked(boolean bool, Direction dir) {
        this.isLocked[dir.getValue()] = bool;
    }

    public String getLockedText(Direction dir) {
        if (this.lockedText[dir.getValue()] != null) {
            return this.lockedText[dir.getValue()];
        }
        return GENERIC_FAILURE; //if no custom locked text has been set, use generic message
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public ArrayList<Enemy> getLivingEnemies() { //only return enemies where isAlive = true
        ArrayList<Enemy> enemyList = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isAlive())
                enemyList.add(e);
        }
        return enemyList;
    }

    public Room[] getExits() { return this.exits; }

    public Room getExit(Direction dir) { return this.exits[dir.getValue()]; }

    public ArrayList<Item> getVisibleItems() {
        ArrayList<Item> items = new ArrayList<>();
        for (Item i : this.getItems()) {
            if (i.getIsVisible()) {
                items.add(i);
            }
        }
        return items;
    }

    public void addItem(Item item) {
        this.getItems().add(item);
    }

    public void addEnemy(Enemy enemy) { this.getEnemies().add(enemy);}

    public void deleteItem(Item item) { this.getItems().remove(item); }

    public void deleteEnemy(Enemy enemy) { this.getEnemies().remove(enemy);}

    public String listAvailableDirections() { //given a room, list all directions with rooms set
        String availableDirections = "";
        for (int i = 0; i < 4; i++) {
            if (exits[i]!=null) {
                availableDirections = availableDirections.concat(" " + Direction.values()[i].name());
            }
        }
        return availableDirections;
    }

    public boolean checkForExit(Direction dir) { //check that exit is assigned to this room in given direction
        Room r = this.exits[dir.getValue()];
        return r != null;
    }

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

    public boolean containsItem(Item item) { //check if room contains given item
        for (Item i : this.items) {
            if (i.compareItem(item)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsEnemy(Enemy enemy) { //check if room contains given enemy
        for (Enemy e : enemies) {
            if (e.getId() == enemy.getId())
                return true;
        }
        return false;
    }

    public boolean hasItems() {
        return this.items.size() > 0;
    }

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

