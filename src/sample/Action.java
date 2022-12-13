package sample;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This interface is used to handle player input and determine
 * the relevant action sequence.
 * @serial
 * @see java.io.Serializable
 */
public interface Action extends Serializable {
    /**
     * The default Action interface method for processing.
     * @param player The player character performing the action.
     * @param input The string input provided by the player to be processed.
     * @return String Describes the effects of the action performed.
     */

    String process(Player player, ArrayList<String> input);

    /**
     * Helper method for converting string input to a new action event type.
     * @param s The given string to be converted to an action.
     * @return Action This returns the action type.
     */
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

/**
 * Take action that allows a player to acquire an item.
 */
class Take implements Action {
    //if successful, adds indicated item to player's inventory
    /**
     * Overrides Action process method to acquire the given item.
     * Prompts player if no item is given or item was not found.
     * @param player The player character to give the item to.
     * @param input The string input indicating the item to pick up.
     * @return String The message to output to the player upon action completion.
     */
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

/**
 * Drop action that allows a player to drop an item.
 */
class Drop implements Action {
    //if successful, removes indicated item from player's inventory
    /**
     * Overrides Action process method to remove the given item from the given player.
     * Prompts player if no item is given or item was not found.
     * @param player The player character to take the item from.
     * @param input The string input indicating the item to drop.
     * @return String The message to output to the player upon action completion.
     */
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

/**
 * View action that allows a player to view an item.
 */
class View implements Action {
    //if successful, shows player the item's description
    /**
     * Overrides Action process method to view the given item.
     * Prompts player if no item is given or item was not found.
     * @param player The player character trying to view the item.
     * @param input The string input indicating the item to view.
     * @return String The message to output to the player upon action completion.
     */
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

/**
 * Travel action that allows a player to view an item.
 */
class Travel implements Action {
    //transfers the player to room in indicated direction from player's current room
    /**
     * Overrides Action process method to relocate given player to a new room.
     * Works for "north", "east", "west", "south" and initials.
     * Returns a failure message if the room is not accessible/doesn't exist.
     * @param player The player character trying to move rooms.
     * @param input The string input indicating the direction to move from current room.
     * @return String The message to output to the player upon action completion.
     */
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

/**
 * Inventory check action that allows a player to view their inventory.
 */
class ItemCheck implements Action {
    //displays player's inventory information
    /**
     * Overrides Action process method to view player's inventory.
     *
     * @param player The player character inventory to access.
     * @param input Ignored.
     * @return String The message to output to the player upon action completion.
     */
    @Override
    public String process(Player player, ArrayList<String> input) {
        return player.inventoryContentsAsString();
    }
}

/**
 * Location checking action that displays a player's current room/description.
 */
class LocationCheck implements Action {
    //displays the current room name and description
    /**
     * Overrides Action process method to view player's current room/description.
     * @param player The player character to evaluate the current room of.
     * @param input Ignored.
     * @return String The message to output to the player upon action completion..
     */
    @Override
    public String process(Player player, ArrayList<String> input) {
        return player.getBearings();
    }
}

/**
 * Location checking action that displays a player's current room/description.
 * Similar to LocationCheck, but displays additional information about items/enemies.
 */
class SurroundingCheck implements Action {
    //displays current room and items/enemies within it
    /**
     * Overrides Action process method to view player's current room summary.
     *
     * @param player The player character to evaluate the current room of.
     * @param input Ignored.
     * @return String The message to output to the player upon action completion.
     */
    @Override
    public String process(Player player, ArrayList<String> input) {
        return player.checkSurroundings();
    }
}

/**
 * Player checking action that displays a player's information such as name and HP.
 */
class SelfCheck implements Action {
    //displays player's information, such as name and HP
    /**
     * Overrides Action process method to view player's information.
     *
     * @param player The player character who's information is displayed.
     * @param input Ignored.
     * @return String The message to output to the player upon action completion.
     */
    @Override
    public String process(Player player, ArrayList<String> input) {
        return player.viewSelf();
    }
}

/**
 * Help action that displays some tips on available actions.
 */
class Help implements Action {
    //displays some help information for the player
    /**
     * Overrides Action process method to display tips on available actions.
     *
     * @param player The player character.
     * @param input Ignored.
     * @return String The message to output to the player upon action completion.
     */
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

/**
 * Use action that allows a player to use an item alone or on other entities.
 */
class Use implements Action {
    //identifies the type of input (single or multi item) and returns the appropriate use method
    /**
     * Overrides Action process method to use an item or multiple items.
     *
     * @param player The player character who should use the item(s).
     * @param input The string arguments to parse to determine action sequence.
     * @return String The message to output to the player upon action completion.
     */
    @Override
    public String process(Player player, ArrayList<String> input) {
        input.remove(0); //remove action string from input, as it is no longer needed
        ArrayList<Item> interactables = player.getInteractables();
        ArrayList<ArrayList<String>> candidates = WordBuilderTools.buildComplex(input);

        Item item = null;
        for (int i = 0; i < interactables.size() && item == null; i++) {
            for (int j = 0; j < candidates.size() && item == null; j++) {
                for (String string : candidates.get(j)) {
                    if (string.equalsIgnoreCase(interactables.get(i).getName())) {
                        //if current combination of user input matches the name of a player interactable, assign it
                        input = candidates.get(j);
                        input.remove(string);
                        item = interactables.get(i);
                        break;
                    }
                }
            }
        }
        if (item == null) { //no item could be found from user input
            return "You do not have that item.";
        }
        if (input.isEmpty()) //attempt to use item by itself
            return item.use(player);

        if (item.compatibleWithItem()) {
            //need to try locating a second item
            Item item2 = (Item) WordBuilderTools.determineEntityFromInput(player.getInteractables(), input);
            if (item2 == null && item instanceof Container) {
                //extra check for container items, if player is trying to take an item from container
                item2 = (Item) WordBuilderTools.determineEntityFromInput(((Container) item).getItems(), input);
            }
            if (item2 != null) {
                return item.use(player, item2);
            }
        }
        if (item.compatibleWithEnemy()) {
            //must specifically find an enemy for weapon
            Enemy enemy = (Enemy) WordBuilderTools.determineEntityFromInput(player.getCurrentRoom().getEnemies(), input);
            if (enemy != null) {
                return item.use(player, enemy);
            }

        }
        return "Cannot do that.";
    }
}