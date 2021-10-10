package sample;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    public static String GENERIC_FAILURE = "You are unable to travel that way currently.";

    static final AtomicLong NEXT_ID = new AtomicLong(0);
    final long id = NEXT_ID.getAndIncrement();

    private String name;
    private String description;
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Npc> npcs = new ArrayList<>();

    private boolean[] isLocked = new boolean[4];
    private String[] lockedText = new String[4];
    private Room[] exits = new Room[4];

    //for when no passage exists, as opposed to a locked passage

    public Room() {
        this.name = "Room 001";
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

    public ArrayList<Npc> getNpcs() { return npcs; }

    public boolean[] getIsLocked() { return this.isLocked; }

    public boolean isDirectionBlocked(Direction dir) { return this.isLocked[dir.getValue()]; }

    public void setIsLocked(boolean bool, Direction dir) {
        this.isLocked[dir.getValue()] = bool;
    }

    public String getLockedText(Direction dir) {
        if (this.lockedText[dir.getValue()] != null) {
            return this.lockedText[dir.getValue()];
        }
        return GENERIC_FAILURE;
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

    public void deleteItem(Item item) { this.getItems().remove(item); }

    public void addNpc(Npc npc) { this.getNpcs().add(npc); }

    public void deleteNpc(Npc npc) { this.getNpcs().remove(npc); }


    public boolean checkForExit(Direction dir) {
        Room r = this.exits[dir.getValue()];
        return r != null;
    }

    public void deleteExit(Direction dir) {
        if (this.checkForExit(dir)) {
            Room other = this.exits[dir.getValue()];
            this.exits[dir.getValue()] = null;
            System.out.println("Connection " + this.getName() + "-" + dir + "->" + other.getName() + " deleted.");
            other.deleteExit(dir.inverseDir());
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
                ", isLocked=" + isLocked +
                ", lockedText=" + lockedText +
                ", exits=" + exits +
                '}';
    }

    public boolean addExit(Direction dir, Room roomToConnect) {
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

    public boolean containsItem(Item item) {
        for (Item i : this.items) {
            if (i.compareItem(item)) {
                return true;
            }
        }
        return false;
    }

    public boolean compareRoom(Room room) {
        return room.getId() == this.getId();
    }

    /**
     *     public boolean createPassage(Room room2, Direction dir, int id, String name, boolean locked,
     *                                  boolean[] conditions, String success, String failure) {
     *         boolean p = (this.checkForPassage(dir) && this.getConnectedRoom(dir).checkForPassage(dir.inverseDir()));
     *         while (p) {
     *                 Scanner s = new Scanner(System.in);
     *                 System.out.println("Do you wish to overwrite the passage for " + dir.name()
     *                                     + " of " + this.getName() + "? Y/N\n");
     *                 String input = s.next();
     *                 if (input.equals("Y") || input.equals("y")) {
     *                     Room other = this.getConnectedRoom(dir); //remove connection from both ends
     *                     other.clearDir(dir.inverseDir());
     *                     this.clearDir(dir);
     *                     p = false;
     *                 }
     *                 else if (input.equals("N") || input.equals("n")) {
     *                     return false;
     *                 }
     *             }
     *         Passage passage = new Passage(id, name, locked, conditions, new Room[]{this, room2}, success, failure); //todo read this data
     *         this.getPassages()[dir.getValue()] = passage;
     *         dir = dir.inverseDir();
     *         room2.getPassages()[dir.getValue()] = passage;
     *         return true;
     *     }
     *
     *         public void clearDir(Direction dir) {
     *         this.getPassages()[dir.getValue()] = null;
     *     }
     *
     *
     public boolean checkForPassage(Direction dir) {
     Passage d = this.getPassages()[dir.getValue()];
     return d != null;
     }

     public boolean addConnection(Direction dir, Room roomToConnect) {
     boolean p = (this.getConnections().get(dir) != null);
     while (p) {
     Scanner s = new Scanner(System.in);
     System.out.println("Do you wish to overwrite the passage for " + dir.name()
     + " of " + this.getName() + "? Y/N\n");
     String input = s.next();
     if (input.equals("Y") || input.equals("y")) {
     Room other = this.getConnections().get(dir); //remove connection from both ends
     other.getConnections().remove(dir.inverseDir());
     this.getConnections().remove(dir);
     p = false;
     }
     else if (input.equals("N") || input.equals("n")) {
     return false;
     }
     }
     this.connections.put(dir, roomToConnect);
     roomToConnect.connections.put(dir.inverseDir(), this);
     return true;
     }

     public Room getConnectedRoom(Direction dir) {
     Passage p = this.getPassages()[dir.getValue()];
     if (p.getRooms()[0] != this)
     return p.getRooms()[0];
     else
     return p.getRooms()[1];
     }

     public String deleteConnection(Direction dir) {
     if (this.hasConnection(dir)) {
     Room other = this.getConnections().get(dir);
     other.getConnections().remove(dir.inverseDir());
     this.getConnections().remove(dir);
     return "Deleted " + this.getName() + " <--> " + other.getName();
     }
     return "No connection found (" + this.getName() + " - " + dir + ")";
     }
     */
}

