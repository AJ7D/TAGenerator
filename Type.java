package sample;

public enum Type {
    DEFAULT("Default"), KEY("Key"), CONSUMABLE("Consumable");

    private final String string;

    Type(String str) {
        this.string = str;
    }

    public String getString() {
        return this.string;
    }
}
