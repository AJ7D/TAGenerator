package sample;

import java.io.Serializable;
import java.util.ArrayList;

public interface Action extends Serializable {
    String process(Player player, ArrayList<String> input);

    static Action stringToAction(String s) {
        //interpret string as an action
        switch (s) {
            case "Use":
                return new Use();
            case "Take":
                return new Take();
            case "Drop":
                return new Drop();
            case "View":
                return new View();
            default:
                return null;
        }
    }

}

class Take implements Action {
    //if successful, adds indicated item to player's inventory
    @Override
    public String process(Player player, ArrayList<String> input) {
        if (input.size() == 1) { //player has not provided an item to take
            return "What do you want to take?";
        }
        else {
            return player.acquire(WordBuilderTools.buildSimple(input));
        }
    }
}

class Drop implements Action {
    //if successful, removes indicated item from player's inventory
    @Override
    public String process(Player player, ArrayList<String> input) {
        if (input.size() == 1) { //player has not provided an item to drop
            return "What do you want to drop?";
        }
        else {
            return player.drop(WordBuilderTools.buildSimple(input));
        }
    }
}

class View implements Action {
    //if successful, shows player the item's description
    @Override
    public String process(Player player, ArrayList<String> input) {
        if (input.size() == 1) { //player has not provided an item to view
            return "What do you want to view?";
        }
        else {
            return player.viewItem(WordBuilderTools.buildSimple(input));
        }
    }
}

class Travel implements Action {
    //transfers the player to room in indicated direction from player's current room
    @Override
    public String process(Player player, ArrayList<String> input) {
        int index = input.size()-1;
        switch (input.get(index)) {
            case "north": case "n":
                return player.travel(Direction.NORTH);
            case "east": case "e":
                return player.travel(Direction.EAST);
            case "west": case "w":
                return player.travel(Direction.WEST);
            case "south": case "s":
                return player.travel(Direction.SOUTH);
            default:
                if (index == 0) { //not indicated a direction, only to travel
                    return "Where do you want to go?";
                } //no such direction exists
                return "You cannot go that way.";
        }
    }
}

class ItemCheck implements Action {
    //displays player's inventory information
    @Override
    public String process(Player player, ArrayList<String> input) {
        return player.checkInventory();
    }
}

class LocationCheck implements Action {
    //displays the current room name and description
    @Override
    public String process(Player player, ArrayList<String> input) {
        return player.getBearings();
    }
}

class SurroundingCheck implements Action {
    //displays current room and items/enemies within it
    @Override
    public String process(Player player, ArrayList<String> input) {
        return player.checkSurroundings();
    }
}

class SelfCheck implements Action {
    //displays player's information, such as name and HP
    @Override
    public String process(Player player, ArrayList<String> input) {
        return player.viewSelf();
    }
}

class Help implements Action {
    //displays some help information for the player
    @Override
    public String process(Player player, ArrayList<String> input) {
        return "Welcome to the help section! Be sure to enter\n" +
                "commands in any of the following formats:\n" +
                "[action]\n[action] [item]\n[action] [item] [item 2]\n" +
                "Some basic commands include 'get [item]',\n"+
                "'drop [item]', 'view [item]', 'go north' (or simply 'n'),\n" +
                "'inventory', 'surroundings' and 'self'. Trying these commands\n" +
                "can help if you are stuck.";
    }
}

class Use implements Action {
    //identifies the type of input (single or multi item) and returns the appropriate use method
    @Override
    public String process(Player player, ArrayList<String> input) {
        input.remove(0); //remove action string from input, as it is no longer needed
        Item item = null; //item to find
        ArrayList<String> remaining = new ArrayList<>(); //for storing unused input arguments
        for (Item i : player.getInteractables()) {
            for (ArrayList<String> combi : WordBuilderTools.buildComplex(input)) {
                for (String string : combi) {
                    if (string.equalsIgnoreCase(i.getName())) {
                        //if current combination of user input matches the name of a player interactable, assign it
                        item = i;
                        remaining = combi;
                        System.out.println("remaining = " + remaining);
                        remaining.remove(string); //store any remaining input to test for multi item action
                        System.out.println("remaining after removal = " + remaining);
                        break;
                    }
                }
            }
        }

        if (item == null) { //no item could be found from user input
            return "You do not have that item.";
        }
        if (remaining.isEmpty()) //attempt to use item by itself
            return item.use(player);

        if (item instanceof Key || item instanceof Container) {
            //need to try locating a second item
            Item item2 = null;
            for (Item i : player.getInteractables()) {
                for (ArrayList<String> combi : WordBuilderTools.buildComplex(remaining)) {
                    for (String string : combi) {
                        if (string.equalsIgnoreCase(i.getName())) {
                            item2 = i;
                            break;
                        }
                    }
                }
            }
            if (item2 == null && item instanceof Container) {
                //extra check for container items, if player is trying to take an item from container
                for (Item i : ((Container) item).getItems()) {
                    for (ArrayList<String> combi : WordBuilderTools.buildComplex(remaining)) {
                        for (String string : combi) {
                            if (string.equalsIgnoreCase(i.getName())) {
                                item2 = i;
                                break;
                            }
                        }
                    }
                }
            }
            if (item2 == null) {
                return "Cannot find that item.";
            }
            return item.use(player, item2);
        }

        if (item instanceof Weapon) {
            //must specifically find an enemy for weapon
            Enemy enemy = null;
            for (Enemy e : player.getCurrentRoom().getEnemies()) {
                for (ArrayList<String> combi : WordBuilderTools.buildComplex(remaining)) {
                    for (String string : combi) {
                        if (string.equalsIgnoreCase(e.getName())) {
                            enemy = e;
                            break;
                        }
                    }
                }
            }
            if (enemy == null) {
                return "Cannot find that enemy.";
            }
            return item.use(player, enemy);
        }
        return item.use(player);
    }
}