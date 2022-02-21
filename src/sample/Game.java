package sample;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private ArrayList<Room> gameMap = new ArrayList<>();
    private ArrayList<Item> gameItems = new ArrayList<>();
    private Player player = new Player("Player");
    private Room startingRoom = new Room();

    private HashMap<String, Action> grammar = defineGrammar();

    public Game() {
        gameMap.add(startingRoom);
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

    public ArrayList<Room> getGameMap() { return this.gameMap; }

    public ArrayList<Item> getGameItems() {
        return gameItems;
    }

    public Room getRoom(String str) {
        for (Room r : this.gameMap) {
            if (str.equals(r.getName())) {
                return r;
            }
        }
        System.out.println("ERROR: Room not found.");
        return null;
    }

    public Item getItem(String str) {
        for (Item i : this.gameItems) {
            if (str.equals(i.getName())) {
                return i;
            }
        }
        System.out.println("ERROR: Item not found.");
        return null;
    }

    public boolean createRoom(Room room) {
        gameMap.add(room);
        return true;
    }

    public void createRoom(String name, String description, Room[] exits) {
        Room room = new Room(name, description, exits);
        gameMap.add(room);
    }

    public boolean createItem(Item item) {
        gameItems.add(item);
        return true;
    }

    public void createItem(String name, String description, boolean isVisible, boolean isCarry, boolean startWith) {
        Item item = new Item(name, description, isVisible, isCarry, startWith);
        gameItems.add(item);
    }

    public void updateRoom(Room room) {
        for (Room r : gameMap) {
            if (r.getId() == (room.getId())) {
                r = room;
                System.out.println("Room " + r + " was updated.");
                return;
            }
        }
        gameMap.add(room);
        System.out.println("Room " + room + "was added.");
    }

    public void updateItem(Item item) {
        for (Item i : gameItems) {
            if (i.getId() == (item.getId())) {
                i = item;
                System.out.println("Item " + i + " was updated.");
                return;
            }
        }
        gameItems.add(item);
        System.out.println("Item " + item + "was added.");
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

    public void deleteItem(Item item) {
        gameItems.remove(item);
        Room room = findRoomWithItem(item);
        if (room != null) {
            room.deleteItem(item);
            System.out.println("Deleted " + item + " from " + room);
            return;
        }
        if (player.getInventory().getContents().contains(item)) {
            player.getInventory().removeItem(item);
            System.out.println("Deleted " + item + " from player inventory.");
            return;
        }
        System.out.println("Item was not found.");
    }

    public Room findRoomWithItem(Item item) {
        for (Room r : gameMap) {
            if (r.containsItem(item)) {
                return r;
            }
        }
        return null;
    }

    public void connectRooms(Room r1, Direction dir, Room r2) {
        if (r1 == r2) {
            System.out.println("Cannot connect a room to itself (" + r1.getName() + ")");
            return;
        }
        r1.addExit(dir, r2);
        if (!gameMap.contains(r1)) {
            gameMap.add(r1);
        }
        if (!gameMap.contains(r2)) {
            gameMap.add(r2);
        }
        System.out.println("Connected rooms " + r1.getName() + " + " + r2.getName());
        //System.out.println("gameMap contents: " + this.getGameMap());
    }

    public Room findItemLoc(Item item) {
        for (Room r : this.gameMap) {
            if (r.containsItem(item)) {
                return r;
            }
        }
        System.out.println("WARNING: Item " + item + " was not found in any room.");
        return null;
    }

    public void saveGameData(File file) throws IOException {
        FileOutputStream fileOutputStream
                = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(this);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public void loadGameData(String title) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream
                = new FileInputStream(title + ".txt");
        ObjectInputStream objectInputStream
                = new ObjectInputStream(fileInputStream);
        Game g2 = (Game) objectInputStream.readObject();
        objectInputStream.close();

        System.out.println(g2.getTitle());
        System.out.println(g2.getGameMap());
        this.title = g2.getTitle();
        this.gameMap = g2.getGameMap();
        this.gameItems = g2.getGameItems();
    }

    public HashMap<String, Action> getGrammar() {
        return this.grammar;
    }

    public void addToGrammar(String verb, Action action) {
        this.grammar.put(verb, action);
    }

    public void removeFromGrammar(String verb) {
        this.grammar.remove(verb);
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

        g.put("eat", new Use()); //TODO remove
        return g;
    }

    public static void main(String[] args) {

    }
}

