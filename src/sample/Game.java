package sample;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private ArrayList<Room> gameMap = new ArrayList<>();
    private ArrayList<Item> gameItems = new ArrayList<>();
    private ArrayList<Enemy> gameEnemies = new ArrayList<>();

    private Player player = new Player("Player");
    private Room startingRoom = new Room();

    private Room winCondition = startingRoom;

    private HashMap<String, Action> grammar = defineGrammar();

    public Game() {
        gameMap.add(startingRoom);
    }

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

    public Game(String title) {
        gameMap.add(startingRoom);
        this.title = title;
    }

    public String getTitle() { return this.title; }

    public void setTitle(String newTitle) { this.title = newTitle; }

    public Player getPlayer() { return this.player; }

    public Room getStartingRoom() { return this.startingRoom; }

    public void setStartingRoom(Room newRoom) { this.startingRoom = newRoom; }

    public Room getWinCondition() {
        return winCondition;
    }

    public void setWinCondition(Room winCondition) {
        this.winCondition = winCondition;
    }

    public boolean isWon() {
        return player.getCurrentRoom() == winCondition;
    }

    public ArrayList<Room> getGameMap() { return this.gameMap; }

    public ArrayList<Item> getGameItems() {
        return gameItems;
    }

    public ArrayList<Enemy> getGameEnemies() {
        return gameEnemies;
    }

    public ArrayList<Item> getContainers() {
        //returns all items of type container
        if (this.getGameItems().isEmpty())
            return null;
        ArrayList<Item> containers = new ArrayList<>();
        for (Item i : this.getGameItems()) {
            if (i instanceof Container) {
                containers.add(i);
            }
        }
        return containers;
    }

    public Room getRoom(Long id) { //find room by unique id
        for (Room r : this.gameMap) {
            if (id == r.getId()) {
                return r;
            }
        }
        return null;
    }

    public Item getItem(Long id) { //find item by unique id
        for (Item i : this.gameItems) {
            if (id == i.getId()) {
                return i;
            }
        }
        return null;
    }

    public Enemy getEnemy(Long id) { //find enemy by unique id
        for (Enemy e : this.gameEnemies) {
            if (id == e.getId()) {
                return e;
            }
        }
        return null;
    }

    public void updateRoom(Room room) { //change attributes of an established room
        for (Room r : gameMap) {
            if (r.getId() == (room.getId())) {
                r = room;
                System.out.println("Room " + r + " was updated.");
                return;
            }
        }
        gameMap.add(room); //room wasn't found, so add it
        System.out.println("Room " + room + "was added.");
    }

    public void updateItem(Item item) { //change attributes of an established item
        for (Item i : gameItems) {
            if (i.getId() == (item.getId())) {
                i = item;
                System.out.println("Item " + i + " was updated.");
                return;
            }
        }
        gameItems.add(item); //item wasn't found, so add it
        System.out.println("Item " + item + "was added.");
    }

    public void updateEnemy(Enemy enemy) { //change attributes of an established enemy
        for (Enemy e : gameEnemies) {
            if (e.getId() == (enemy.getId())) {
                e = enemy;
                System.out.println("Enemy " + e + " was updated.");
                return;
            }
        }
        gameEnemies.add(enemy); //enemy wasn't found, so add it
        System.out.println("Enemy " + enemy + "was added.");
    }

    public void deleteRoom(Room room) {
        for (Room r : gameMap) {
            if (r.getId() == room.getId()) {
                for (Item i : r.getItems()) { //delete all items attached to this room
                    System.out.println("Deleted item: " + i.getId() + i.getName());
                    gameItems.remove(i);
                }
                for (Direction dir : Direction.values()) { //sever all room connections before deletion
                    r.deleteExit(dir);
                }
                System.out.println("Deleted room: " + r.getId() + r.getName());
                gameMap.remove(r);
                return;
            }
        }
        System.out.println("Room not found.");
    }

    public void deleteItem(Item item) { //remove an item from the game, severing any established connections
        if (item instanceof Container) {
            this.emptyContainer((Container) item); //all items in container moved to container's room
        }
        deleteItemInstances(item); //remove item from any rooms, containers, inventories, etc.
        deleteEntity(gameItems, item); //remove item from game items
    }

    public void deleteEntity(Collection<? extends Entity> collection, Entity entity) {
        //for implementing any conditional checks
        for (Entity e : collection) {
            if (e.equals(entity)) {
                collection.remove(entity);
                return;
            }
        }
    }

    public void deleteEnemy(Enemy enemy) { //remove enemy from game
        deleteEntity(gameEnemies, enemy);
        Room room = enemy.getCurrentRoom();

        if (!enemy.getInventory().getContents().isEmpty()) {
            //place enemy inventory into enemy's current room
            room.getItems().addAll(enemy.getInventory().getContents());
        }
        if (room != null) {
            //delete enemy from room it is associated with
            room.deleteEnemy(enemy);
            return;
        }
        System.out.println("Enemy was not found.");
    }

    public void connectRooms(Room r1, Direction dir, Room r2) throws IllegalRoomConnection {
        if (r1 == r2) {
            throw new IllegalRoomConnection("Room cannot be connected to itself (" + r1.getName() + ")");
        }
        r1.addExit(dir, r2);
        if (!gameMap.contains(r1)) {
            gameMap.add(r1);
        }
        if (!gameMap.contains(r2)) {
            gameMap.add(r2);
        }
        System.out.println("Connected rooms " + r1.getName() + " + " + r2.getName());
    }

    public Room findItemLocRoom(Item item) { //find room of an item, or entity holding item
        for (Room r : this.gameMap) {
            if (r.containsItem(item)) {
                return r;
            }
            for (Item i : r.getItems()) {
                if (i instanceof Container) {
                    if (recSearchItemContains((Container) i, item))
                        return r;
                }
            }
            for (Enemy e : r.getEnemies()) {
                if (recSearchEnemyContains(e, item))
                    return r;
            }
        }
        System.out.println("WARNING: Item " + item + " was not found in any room.");
        return null;
    }

    public boolean recSearchItemContains(Container rootContainer, Item itemToFind) {
        //recursively searches for an item, handling containers within containers
        if (rootContainer.getItems().isEmpty())
            return false;
        for (Item i : rootContainer.getItems()) {
            if (i.getId() == itemToFind.getId()) {
                return true; //item has been found
            }
            if (i instanceof Container) {
                return recSearchItemContains((Container) i, itemToFind); //search next container
            }
        }
        return false; //item was not found in any container
    }

    public boolean recSearchEnemyContains(Enemy rootEnemy, Item itemToFind) {
        //recursively searches for an item, handling containers within enemies
        if (rootEnemy.getInventory().getContents().isEmpty())
            return false;
        for (Item i : rootEnemy.getInventory().getContents()) {
            if (i.getId() == itemToFind.getId()) {
                return true; //item found
            }
            if (i instanceof Container) {
                return recSearchItemContains((Container) i, itemToFind); //search container
            }
        }
        return false; //item not found
    }

    public void deleteItemInstances(Item item) { //delete all item instances in the game
        for (Room r : gameMap) {
            r.getItems().remove(item); //remove from any rooms
        }
        for (Item i : gameItems) {
            if (i instanceof Container) {
                ((Container) i).getItems().remove(item); //remove from any containers
            }
        }
        for (Enemy e : gameEnemies) {
            e.getInventory().getContents().remove(item); //remove from any enemy inventories
        }
        player.getInventory().getContents().remove(item); //remove from player inventory
    }

    public Entity findItemInstance(Item item) { //locate primary item holder
        for (Room r : gameMap) {
            if (r.getItems().contains(item)) //item is in a room
                return r;
        }
        for (Item i : gameItems) {
            if (i instanceof Container) {
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

    public void saveGameData(File file) throws IOException { //write game data to a file
        FileOutputStream fileOutputStream
                = new FileOutputStream(file); //select file to output data
        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(fileOutputStream); //get data to write to file
        objectOutputStream.writeObject(this); //write data to file
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public HashMap<String, Action> getGrammar() {
        return this.grammar;
    }

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

    public void emptyContainer(Container container) { //place container contents in container's room when deleted
        if (container.getItems().isEmpty())
            return;
        this.findItemLocRoom(container).getItems().addAll(container.getItems());
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

