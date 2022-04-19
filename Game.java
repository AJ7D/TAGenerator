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

    public ArrayList<Room> getGameMap() { return this.gameMap; }

    public ArrayList<Item> getGameItems() {
        return gameItems;
    }

    public ArrayList<Enemy> getGameEnemies() {
        return gameEnemies;
    }

    public ArrayList<Item> getContainers() {
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

    public Room getRoom(Long id) {
        for (Room r : this.gameMap) {
            if (id == r.getId()) {
                return r;
            }
        }
        return null;
    }

    public Item getItem(Long id) {
        for (Item i : this.gameItems) {
            if (id == i.getId()) {
                return i;
            }
        }
        return null;
    }

    public Enemy getEnemy(Long id) {
        for (Enemy e : this.gameEnemies) {
            if (id == e.getId()) {
                return e;
            }
        }
        return null;
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

    public void updateEnemy(Enemy enemy) {
        for (Enemy e : gameEnemies) {
            if (e.getId() == (enemy.getId())) {
                e = enemy;
                System.out.println("Enemy " + e + " was updated.");
                return;
            }
        }
        gameEnemies.add(enemy);
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

    public void deleteItem(Item item) {
        if (item instanceof Container) {
            this.emptyContainer((Container) item);
        }
        deleteItemInsances(item);
        deleteEntity(gameItems, item);
    }

    public void deleteEntity(Collection<? extends Entity> collection, Entity entity) {
        for (Entity e : collection) {
            if (e.equals(entity)) {
                collection.remove(entity);
                return;
            }
        }
    }

    public void deleteEnemy(Enemy enemy) {
        deleteEntity(gameEnemies, enemy);
        Room room = enemy.getCurrentRoom();

        if (!enemy.getInventory().getContents().isEmpty()) {
            room.getItems().addAll(enemy.getInventory().getContents());
        }
        if (room != null) {
            room.deleteEnemy(enemy);
            System.out.println("Deleted " + enemy + " from " + room);
            return;
        }
        System.out.println("Enemy was not found.");
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

    public Room findItemLocRoom(Item item) {
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
        if (rootContainer.getItems().isEmpty())
            return false;
        for (Item i : rootContainer.getItems()) {
            if (i.getId() == itemToFind.getId()) {
                return true;
            }
            if (i instanceof Container) {
                return recSearchItemContains((Container) i, itemToFind);
            }
        }
        return false;
    }

    public boolean recSearchEnemyContains(Enemy rootEnemy, Item itemToFind) {
        if (rootEnemy.getInventory().getContents().isEmpty())
            return false;
        for (Item i : rootEnemy.getInventory().getContents()) {
            if (i.getId() == itemToFind.getId()) {
                return true;
            }
            if (i instanceof Container) {
                return recSearchItemContains((Container) i, itemToFind);
            }
        }
        return false;
    }

    public void deleteItemInsances(Item item) {
        for (Room r : gameMap) {
            r.getItems().remove(item);
        }
        for (Item i : gameItems) {
            if (i instanceof Container) {
                ((Container) i).getItems().remove(item);
            }
        }
        for (Enemy e : gameEnemies) {
            e.getInventory().getContents().remove(item);
        }
        player.getInventory().getContents().remove(item);
    }

    public Entity findItemInstance(Item item) {
        for (Room r : gameMap) {
            if (r.getItems().contains(item))
                return r;
        }
        for (Item i : gameItems) {
            if (i instanceof Container) {
                if (((Container) i).getItems().contains(item)) {
                    return i;
                }
            }
        }
        for (Enemy e : gameEnemies) {
            if (e.getInventory().getContents().contains(item))
                return e;
        }
        if (player.getInventory().getContents().contains(item))
            return player;
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

    public void emptyContainer(Container container) {
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
        game.deleteItemInsances(item);
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

