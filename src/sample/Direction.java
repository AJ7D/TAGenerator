package sample;

public enum Direction {
    NORTH(0),
    WEST(1),
    EAST(2),
    SOUTH(3),
    UP(4),
    DOWN(5),
    IN(6),
    OUT(7);

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
            case UP:
                newDir = DOWN;
                break;
            case DOWN:
                newDir = UP;
                break;
            case IN:
                newDir = OUT;
                break;
            case OUT:
                newDir = IN;
                break;
        }
        return newDir;
    }
}
