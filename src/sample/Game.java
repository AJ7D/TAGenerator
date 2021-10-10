package sample;

import java.io.*;
import java.util.ArrayList;

public class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private ArrayList<Room> gameMap = new ArrayList<>();
    private ArrayList<Item> gameItems = new ArrayList<>();
    private Player player = new Player("Player");

    public Game() {
    }

    public Game(String title) {
        this.title = title;
    }

    public String getTitle() { return this.title; }

    public Player getPlayer() { return this.player; }

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

    public void createItem(String name, String description, Type type, boolean isVisible, boolean isCarry, boolean startWith) {
        Item item = new Item(name, description, type, isVisible, isCarry, startWith);
        gameItems.add(item);
    }

    public void updateRoom(Room room) {
        for (Room r : gameMap) {
            if (r.getId() == (room.getId())) {
                r = room;
                System.out.println("Room " + room + " was updated.");
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
                System.out.println("Item " + item + " was updated.");
                return;
            }
        }
        gameItems.add(item);
        System.out.println("Item " + item + "was added.");
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

    public void saveGameData() throws IOException {
        FileOutputStream fileOutputStream
                = new FileOutputStream(this.getTitle() + ".txt");
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
    }

    public static void main(String[] args) {

    }
}

