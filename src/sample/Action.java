package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public interface Action extends Serializable {
    String process(Player player, ArrayList<String> input);

    default String wordBuilder(ArrayList<String> input) {
        StringBuilder item = new StringBuilder();

        //concat all words into an item query, ignoring action
        for (int i = 1; i < input.size(); i++) {
            item.append(input.get(i));
            if (i != input.size()-1) {
                item.append(" ");
            }
        }
        System.out.println(item.toString());
        return item.toString();
    }

    default String[] wordBuilderComplex(ArrayList<String> input) {
        //temp function for handling 2 items
        String[] items = new String[2];
        items[1] = input.get(input.size() - 1);

        StringBuilder item = new StringBuilder();
        //concat all words into an item query, ignoring action
        for (int i = 1; i < input.size() - 1; i++) {
            item.append(input.get(i));
            if (i != input.size()-1) {
                item.append(" ");
            }
        }

        for (int i = item.length()-1; i > 0; i--) {
            if (item.charAt(i) == ' ') {
                item.deleteCharAt(i);
            }
            else {
                break;
            }
        }

        items[0] = item.toString();
        System.out.println(Arrays.toString(items));
        return items;
    }
}

class Take implements Action {
    @Override
    public String process(Player player, ArrayList<String> input) {
        if (input.size() == 1) {
            return "What do you want to take?";
        }
        else {
            return player.acquire(wordBuilder(input));
        }
    }
}

class Drop implements Action {
    @Override
    public String process(Player player, ArrayList<String> input) {
        if (input.size() == 1) {
            return "What do you want to drop?";
        }
        else {
            return player.drop(wordBuilder(input));
        }
    }
}

class View implements Action {
    @Override
    public String process(Player player, ArrayList<String> input) {
        if (input.size() == 1) {
            return "What do you want to view?";
        }
        else {
            return player.viewItem(wordBuilder(input));
        }
    }
}

class Travel implements Action {
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
                if (index == 0) {
                    return "Where do you want to go?";
                }
                return "You cannot go that way.";
        }
    }
}

class ItemCheck implements Action {
    @Override
    public String process(Player player, ArrayList<String> input) {
        return player.checkInventory();
    }
}

class LocationCheck implements Action {
    @Override
    public String process(Player player, ArrayList<String> input) {
        return player.getBearings();
    }
}

class SurroundingCheck implements Action {
    @Override
    public String process(Player player, ArrayList<String> input) {
        return player.checkSurroundings();
    }
}

class SelfCheck implements Action {
    @Override
    public String process(Player player, ArrayList<String> input) {
        return player.viewSelf();
    }
}

class Help implements Action {
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
    @Override
    public String process(Player player, ArrayList<String> input) {
        Item item = player.getInventory().findItemByName(wordBuilder(input));
        if (item == null) {
            item = player.getInventory().findItemByName(wordBuilderComplex(input)[0]);
        }
        if (item == null) {
            return "You do not have that item.";
        }

        if (item instanceof Key || item instanceof Container) {
            if (input.size() == 1) {
                return "What are you using " + item.getName() + " on?";
            }
            Item item2 = player.getInventory().findItemByName(wordBuilderComplex(input)[1]);
            if (item2 == null) {
                item2 = player.getCurrentRoom().findItemByName(wordBuilderComplex(input)[1]);
            }
            if (item2 == null) {
                return "Cannot find that item.";
            }
            return item.use(player, item2);
        }

        if (item instanceof Weapon) {
            if (input.size() == 1) {
                return "What are you using " + item.getName() + " on?";
            }
            Character character = player.getCurrentRoom().getNpc(wordBuilderComplex(input)[1]);
            if (character == null) {
                return "Cannot find that enemy.";
            }
            return item.use(player, character);
        }
        return item.use(player);
        //TODO
    }
}