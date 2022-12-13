package sample.game;

import java.io.Serializable;
import java.util.ArrayList;

public class Npc extends Character implements Serializable {

    private final ArrayList<String> dialogue = new ArrayList<>();
    private int currentDialogue;

    Npc(String name) {
        super(name);
        currentDialogue = 0;
    }

    Npc(String name, Room currentRoom) {
        super(name, currentRoom);
        currentDialogue = 0;
    }

    public void addDialogue(String d) {
        this.dialogue.add(d);
    }

    public void removeDialogue(int i) {
        if (i < 0 || i > (dialogue.size()-1)) {
            System.out.println("No dialogue at index " + i);
            return;
        }
        this.dialogue.remove(i);
    }

    public String speak() {
        if (dialogue.size() == 0) {
            return "You cannot talk with " + this.getName() + " right now.";
        }
        return dialogue.get(currentDialogue);
    }

    public String advanceDialogue() {
        if (currentDialogue < (dialogue.size()-1)) {
            currentDialogue++;
        }
        return speak();
    }

    public void advanceDialogueSilent() {
        currentDialogue++;
    }
}

