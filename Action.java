package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public interface Action extends Serializable {
    String process(Player player, ArrayList<String> input);

    static Action stringToAction(String s) {
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

    default ArrayList<ArrayList<String>> wordBuilderComplexer(ArrayList<String> input) {
        ArrayList<ArrayList<String>> combinations = new ArrayList<>();
        //concat all words into an item query, ignoring action

        for (int i = 0; i < input.size(); i++) {
            StringBuilder item = new StringBuilder();
            StringBuilder item2 = new StringBuilder();
            for (int j = 0; j < i; j++) {
                item.append(input.get(j));
                item.append(" ");
            }
            for (int j = i; j < input.size(); j++) {
                item2.append(input.get(j));
                item2.append(" ");
            }
            ArrayList<String> combi = new ArrayList<>();
            combi.add(item.toString().trim());
            combi.add(item2.toString().trim());
            combinations.add(combi);
        }
        return combinations;
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
        input.remove(0);
        Item item = null;
        ArrayList<String> remaining = new ArrayList<>();
        for (Item i : player.getInteractables()) {
            for (ArrayList<String> combi : wordBuilderComplexer(input)) {
                for (String string : combi) {
                    if (string.equalsIgnoreCase(i.getName())) {
                        item = i;
                        remaining = combi;
                        remaining.remove(string);
                        break;
                    }
                }
            }
        }

        if (item == null) {
            return "You do not have that item.";
        }

        if (item instanceof Key || item instanceof Container) {
            if (remaining.isEmpty()) {
                return "What are you trying to do with " + item.getName() + "?";
            }
            Item item2 = null;
            for (Item i : player.getInteractables()) {
                for (ArrayList<String> combi : wordBuilderComplexer(remaining)) {
                    for (String string : combi) {
                        if (string.equalsIgnoreCase(i.getName())) {
                            item2 = i;
                            break;
                        }
                    }
                }
            }
            if (item2 == null && item instanceof Container) {
                //TODO clean up
                for (Item i : ((Container) item).getItems()) {
                    for (ArrayList<String> combi : wordBuilderComplexer(remaining)) {
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
            if (remaining.isEmpty()) {
                return "What are you using " + item.getName() + " on?";
            }
            Enemy enemy = null;
            for (Enemy e : player.getCurrentRoom().getEnemies()) {
                for (ArrayList<String> combi : wordBuilderComplexer(remaining)) {
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
        //TODO
    }
}