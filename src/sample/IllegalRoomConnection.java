package sample;

/** Exception when trying to connect two rooms that cannot be connected to each other.*/
public class IllegalRoomConnection extends Exception {

    public IllegalRoomConnection(String errorMessage) {
        super(errorMessage);
    }
}
