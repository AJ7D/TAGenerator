package sample;

/** Enum for holding direction values NORTH, WEST, EAST and SOUTH.*/
public enum Direction {
    NORTH(0),
    WEST(1),
    EAST(2),
    SOUTH(3);

    /** Associates each direction to an int value.*/
    private final int value;

    /** Creates a direction with a value.
     * @param value The int value to set to the direction.*/
    Direction(int value) {
        this.value = value;
    }

    /** Gets int the value of the direction.
     * @return int The value of the direction.*/
    public int getValue() {
        return this.value;
    }

    /** Gets the inverse of the direction, i.e. NORTH becomes SOUTH, WEST becomes EAST.
     * @return Direction The inverse direction.*/
    public Direction inverseDir() {
        //return opposite direction e.g. input north returns south
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
