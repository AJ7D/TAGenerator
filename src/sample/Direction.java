package sample;

public enum Direction {
    NORTH(0),
    WEST(1),
    EAST(2),
    SOUTH(3);

    private final int value;

    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public Direction inverseDir() {
        Direction newDir = this;
        switch (this) {
            case NORTH:
                newDir = SOUTH;
                break;
            case WEST:
                newDir = EAST;
                break;
            case EAST:
                newDir = WEST;
                break;
            case SOUTH:
                newDir = NORTH;
                break;
        }
        return newDir;
    }
}
