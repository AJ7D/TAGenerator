package sample;

import java.util.ArrayList;

public interface Action {
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