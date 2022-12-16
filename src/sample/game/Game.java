package sample.game;

import sample.generator.IllegalRoomConnection;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/** Class for storing game entity data and player stats. Editable in generator and playable in engine.
 * @serial */
public class Game implements Serializable {
    /** The serial version UID of the game.*/
    private static final long serialVersionUID = 1L;

    /** The title of the game, displayed when game is loaded.*/
    private String title;
    /** ArrayList of game's rooms.*/
    private HashSet<Room> gameMap = new HashSet<>();
    /** ArrayList of game's items.*/
    private HashSet<Item> gameItems = new HashSet<>();
    /** ArrayList of game's enemies.*/
    private HashSet<Enemy> gameEnemies = new HashSet<>();

    /** The player character for this game.*/
    private Player player = new Player("Player");
    /** The room that the player character should start in if no save state loaded.*/
    private Room startingRoom = new Room();

    /** The room the player character must reach to win the game.*/
    private Room winCondition = startingRoom;

    /** Defines default grammar for the game to allow player to perform actions.
     * @see Action*/
    private HashMap<String, Action> grammar = defineGrammar();

    /** Default constructor for a game. Adds a default room to the game map for a valid game file.*/
    public Game() {
        gameMap.add(startingRoom);
    }

    /** Copy constructor for a game.
     * @param game The game to be copied.*/
    public Game(Game game) {
        this.title = game.title;
        this.gameMap = game.gameMap;
        this.gameItems = game.gameItems;
        this.gameEnemies = game.gameEnemies;
        this.player = game.player;
        this.startingRoom = game.startingRoom;
        this.winCondition = game.winCondition;
        this.grammar = game.grammar;
    }

    /** Constructor for game that takes a game title. Adds a default room to the game map.
     * @param title The title of the created game.*/
    public Game(String title) {
        gameMap.add(startingRoom);
        this.title = title;
    }

    /** Gets the title of the game.
     * @return String The title of the game.*/
    public String getTitle() { return this.title; }

    /** Sets the title of the game.
     * @param newTitle The new title of the game to be set.*/
    public void setTitle(String newTitle) { this.title = newTitle; }

    /** Gets the player associated with the game.
     * @return Player The player associated with the game.*/
    public Player getPlayer() { return this.player; }

    /** Gets the room that the player should start in.
     * @return Room The starting room set for the game.*/
    public Room getStartingRoom() { return this.startingRoom; }

    /** Sets the starting room for the game.
     * @param newRoom The room to be set as the starting room.*/
    public void setStartingRoom(Room newRoom) { this.startingRoom = newRoom; }

    /** Gets the room that the player must reach to win the game.
     * @return Room The room that the player must reach to win the game.*/
    public Room getWinCondition() {
        return winCondition;
    }

    /** Sets the winning room for the game.
     * @param winCondition The room to be set as the winning room.*/
    public void setWinCondition(Room winCondition) {
        this.winCondition = winCondition;
    }

    /** Determines if the game has been won by checking if player is in winning room.
     * @return boolean Returns true if the player is in the winning room.*/
    public boolean isWon() {
        return player.getCurrentRoom() == winCondition;
    }

    /** Gets the game map as an ArrayList of Rooms.
     * @return ArrayList The ArrayList of Rooms defined in the game map.
     * @see Room*/
    public HashSet<Room> getGameMap() { return this.gameMap; }

    /** Gets the game items as an ArrayList of Items.
     * @return ArrayList The ArrayList of Items defined in the game items.
     * @see Item*/
    public HashSet<Item> getGameItems() {
        return gameItems;
    }

    /** Gets the game enemies as an ArrayList of Enemies.
     * @return ArrayList The ArrayList of Enemies defined in the game enemies.
     * @see Enemy*/
    public HashSet<Enemy> getGameEnemies() {
        return gameEnemies;
    }

    /** Gets all game items that can contain other game items.
     * @return ArrayList An ArrayList of Containers derived from the game items ArrayList.
     * @see Container*/
    public ArrayList<Item> getContainers() {
        //returns all items of type container
        if (gameItems.isEmpty())
            return null;
        ArrayList<Item> containers = new ArrayList<>();
        for (Item i : gameItems) {
            if (i.getHeldItems() != null) {
                containers.add(i);
            }
        }
        return containers;
    }

    /** Gets a room in the game map by its unique identifier. Can return null.
     * @param id The ID of the room to find.
     * @return Room The room with the corresponding unique identifier.
     * @see Room*/
    public Room getRoom(Long id) { //find room by unique id
        for (Room r : gameMap) {
            if (id == r.getId()) {
                return r;
            }
        }
        return null;
    }

    /** Gets an item in the game's items by its unique identifier. Can return null.
     * @param id The ID of the item to find.
     * @return Item The item with the corresponding unique identifier.
     * @see Item*/
    public Item getItem(Long id) { //find item by unique id
        for (Item i : gameItems) {
            if (id == i.getId()) {
                return i;
            }
        }
        return null;
    }

    /** Gets an enemy in the game's enemies by its unique identifier. Can return null.
     * @param id The ID of the enemy to find.
     * @return Enemy The enemy with the corresponding unique identifier.
     * @see Enemy*/
    public Enemy getEnemy(Long id) { //find enemy by unique id
        for (Enemy e : gameEnemies) {
            if (id == e.getId()) {
                return e;
            }
        }
        return null;
    }

    /** Updates an existing room with the data of the given room.
     * @param room The room data to update with.
     * @see Room*/
    public void updateRoom(Room room) { //change attributes of an established room
        gameMap.add(room); //room wasn't found, so add it
    }

    /** Updates an existing item with the data of the given item.
     * @param item The item data to update with.
     * @see Item*/
    public void updateItem(Item item) { //change attributes of an established item
        gameItems.add(item); //item wasn't found, so add it
    }

    /** Updates an existing enemy with the data of the given enemy.
     * @param enemy The enemy data to update with.
     * @see Enemy*/
    public void updateEnemy(Enemy enemy) { //change attributes of an established enemy
        gameEnemies.add(enemy); //enemy wasn't found, so add it
    }

    /** Delete a given room from the game's map.
     * @param room The room to be deleted from the game map.
     * @see Room*/
    public void deleteRoom(Room room) {
        if (gameMap.contains(room)) {
            for (Item i : room.getItems())
                gameItems.remove(i);
            for (Direction dir : Direction.values())
                room.deleteExit(dir);
            gameMap.remove(room);
        }
    }

    /** Delete a given item from the game's items.
     * @param item The item to be deleted from the game's items.
     * @see Item*/
    public void deleteItem(Item item) { //remove an item from the game, severing any established connections
        if (item.getHeldItems() != null) {
            emptyContainer((Container) item); //all items in container moved to container's room
        }
        deleteItemInstances(item); //remove item from any rooms, containers, inventories, etc.
        gameItems.remove(item);
    }

    /** Delete a given enemy from the game's enemies.
     * @param enemy The enemy to be deleted from the game's enemies.
     * @see Enemy*/
    public void deleteEnemy(Enemy enemy) { //remove enemy from game
        gameEnemies.remove(enemy);
        Room room = enemy.getCurrentRoom();

        if (!enemy.getInventory().getContents().isEmpty()) {
            //place enemy inventory into enemy's current room
            room.getItems().addAll(enemy.getInventory().getContents());
        }
        if (room != null) {
            //delete enemy from room it is associated with
            room.deleteEnemy(enemy);
        }
    }

    /** Add the second given room to the given direction of the first given room.
     * @param r1 The room to connect a room to.
     * @param dir The direction of r1 to add r2 to.
     * @param r2 The second room to connect to r1.
     * @throws IllegalRoomConnection if trying to connect a room to itself.
     * @see Room*/
    public void connectRooms(Room r1, Direction dir, Room r2) throws IllegalRoomConnection {
        if (r1 == r2) {
            throw new IllegalRoomConnection("Room cannot be connected to itself (" + r1.getName() + ")");
        }
        r1.addExit(dir, r2);
        gameMap.add(r1); //only add if doesn't exist yet
        gameMap.add(r2);
        System.out.println("Connected rooms " + r1.getName() + " + " + r2.getName());
    }

    /** Find the room that an item is contained within. Can return null.
     * @param item The item to find.
     * @return Room The room where the item was found.
     * @see Room
     * @see Item */
    public Room findItemLocRoom(Item item) { //find room of an item, or entity holding item
        for (Room r : gameMap) {
            for (Entity e : r.getEntities()) {
                if (e.getId() == item.getId() || (e.getHeldItems() != null && recSearchContains(e, item)))
                    return r;
            }
        }
        return null;
    }

    public boolean recSearchContains(Entity rootEntity, Item itemToFind) {
        //recursively searches for an item, handling containers within containers
        if (rootEntity.getHeldItems().isEmpty())
            return false;
        for (Entity e : rootEntity.getHeldItems()) {
            if (e.getId() == itemToFind.getId()) {
                return true; //item has been found
            }
            if (e.getHeldItems() != null) {
                return recSearchContains(e, itemToFind); //search next container
            }
        }
        return false; //item was not found in any container
    }

    /** Deletes all references of an item then removes it from the game items list.
     * @param item The item to be fully removed.*/
    public void deleteItemInstances(Item item) { //delete all item instances in the game
        for (Room r : gameMap) {
            r.getItems().remove(item); //remove from any rooms
        }
        for (Item i : gameItems) {
            if (i.getHeldItems() != null) {
                ((Container) i).getItems().remove(item); //remove from any containers
            }
        }
        for (Enemy e : gameEnemies) {
            e.getInventory().getContents().remove(item); //remove from any enemy inventories
        }
        player.getInventory().getContents().remove(item); //remove from player inventory
    }

    /** Finds the primary holder of an item, e.g. container, enemy or player holding an item.
     * @param item The item to be found.
     * @return Entity The entity holding the item.
     * @see Entity*/
    public Entity findItemImmediateParent(Item item) { //locate primary item holder
        for (Room r : gameMap) {
            if (r.getItems().contains(item)) //item is in a room
                return r;
        }
        for (Item i : gameItems) {
            if (i.getHeldItems() != null) {
                if (((Container) i).getItems().contains(item)) { //item is in a container
                    return i;
                }
            }
        }
        for (Enemy e : gameEnemies) {
            if (e.getInventory().getContents().contains(item)) //item is in an enemy inventory
                return e;
        }
        if (player.getInventory().getContents().contains(item)) //item is in player inventory
            return player;
        return null; //item could not be found?
    }

    /** Writes the game contents to a given file.
     * @param file The file to write the game contents to
     * @throws IOException if file cannot be written to.*/
    public void saveGameData(File file) throws IOException { //write game data to a file
        FileOutputStream fileOutputStream
                = new FileOutputStream(file); //select file to output data
        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(fileOutputStream); //get data to write to file
        objectOutputStream.writeObject(this); //write data to file
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    /** Returns the grammar associated with the game.
     * @return HashMap Returns the game's defined grammar.
     * @see Action*/
    public HashMap<String, Action> getGrammar() {
        return this.grammar;
    }

    /** Defines the default grammar by populating the grammar HashMap.
     * @return HashMap A HashMap containing the default grammar.
     * @see Action*/
    private HashMap<String, Action> defineGrammar() {
        //default grammar
        HashMap<String, Action> g = new HashMap<>();
        g.put("take", new Take());
        g.put("get", new Take());
        g.put("grab", new Take());
        g.put("pick", new Take());
        g.put("acquire", new Take());

        g.put("drop", new Drop());
        g.put("discard", new Drop());
        g.put("abandon", new Drop());

        g.put("view", new View());
        g.put("look", new View());
        g.put("examine", new View());

        g.put("inventory", new ItemCheck());
        g.put("items", new ItemCheck());
        g.put("belongings", new ItemCheck());

        g.put("location", new LocationCheck());
        g.put("where", new LocationCheck());

        g.put("surroundings", new SurroundingCheck());
        g.put("check", new SurroundingCheck());

        g.put("self", new SelfCheck());

        g.put("go", new Travel());
        g.put("walk", new Travel());
        g.put("travel", new Travel());
        g.put("journey", new Travel());
        g.put("north", new Travel());
        g.put("n", new Travel());
        g.put("east", new Travel());
        g.put("e", new Travel());
        g.put("west", new Travel());
        g.put("w", new Travel());
        g.put("south", new Travel());
        g.put("s", new Travel());

        g.put("!help", new Help());

        g.put("use", new Use()); //TODO remove
        return g;
    }

    /** Gets information about the game's fields.
     * @return String Returns information about the game formatted as a string.*/
    @Override
    public String toString() {
        return "Game{" +
                "title='" + title + '\'' +
                ", gameMap=" + gameMap +
                ", gameItems=" + gameItems +
                ", gameEnemies=" + gameEnemies +
                ", player=" + player +
                ", startingRoom=" + startingRoom +
                ", grammar=" + grammar +
                '}';
    }

    /** Removes contents from the given container and places them into the room holding the container.
     * @param container The container to empty.
     * @see Container*/
    public void emptyContainer(Container container) { //place container contents in container's room when deleted
        if (container.getItems().isEmpty())
            return;
        findItemLocRoom(container).getItems().addAll(container.getItems());
        container.getItems().clear();
    }

    public static void main(String[] args) {
        Container container = new Container();
        Item item = new Item();

        Game game = new Game();
        game.gameItems.add(container);
        game.gameItems.add(item);
        container.addItem(item);
        System.out.println(container.getItems().toString());
        game.deleteItemInstances(item);
        System.out.println(container.getItems().toString());

        Room room = new Room();
        Container container2 = new Container();
        Consumable apple = new Consumable();

        container.addItem(container2);
        container2.addItem(apple);
        room.addItem(container);
        game.getGameMap().add(room);
        System.out.println(game.findItemLocRoom(apple));
    }
}

