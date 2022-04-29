package sample;

import java.util.ArrayList;

/** Class for providing additional user input parsing methods used in the game engine.
 * @see EngineController*/
public class WordBuilderTools { //methods for parsing user input

    /** Assumes only one item is being taken as input. Builds the passed ArrayList
     * into a single string for item lookup.
     * @param input The input to convert to a string.
     * @return String The formatted string used for lookup.*/
    public static String buildSimple(ArrayList<String> input) {
        //takes user input and reformats it into a multi word string for lookup
        StringBuilder item = new StringBuilder();

        //concat all words into an item query, ignoring action
        for (int i = 1; i < input.size(); i++) {
            item.append(input.get(i));
            if (i != input.size()-1) {
                item.append(" ");
            }
        }
        return item.toString();
    }

    /** Assumes that input can be one item or more, and creates a list of potential combinations.
     * This way, users can have some flexibility in using words with multiple spaces while
     * distinguishing numerous items.
     * @param input The input to be converted to a list of potential string combinations.
     * @return ArrayList The ArrayList of ArrayLists denoting possible combinations for further lookup.*/
    public static ArrayList<ArrayList<String>> buildComplex(ArrayList<String> input) {
        //takes user input and creates an arraylist of arraylists containing
        //possible string splits to identify two items in an input
        ArrayList<ArrayList<String>> combinations = new ArrayList<>();

        if (input.size() == 1) { //no point trying to separate single word
            combinations.add(input);
            return combinations;
        }
        for (int i = 0; i < input.size(); i++) {
            //initialise two new potential items for current arrayList
            StringBuilder item = new StringBuilder();
            StringBuilder item2 = new StringBuilder();
            for (int j = 0; j < i; j++) {
                //build first item string
                item.append(input.get(j));
                item.append(" ");
            }
            for (int j = i; j < input.size(); j++) {
                //build second item string from remaining input
                item2.append(input.get(j));
                item2.append(" ");
            }
            ArrayList<String> combi = new ArrayList<>();
            combi.add(item.toString().trim());
            combi.add(item2.toString().trim()); //remove whitespace
            combinations.add(combi);
        }
        return combinations;
    }
}
