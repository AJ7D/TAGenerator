package sample.game;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/** Generic class for defining elements of all entities that can exist within a game file, e.g. item, enemy, player, room.
 * @serial */
public abstract class Entity implements Serializable {
    /** The serial version UID of the entity.*/
    private static final long serialVersionUID = 1L;
    /** Incremented when a new entity is created.*/
    static final AtomicLong NEXT_ID = new AtomicLong(0); //generate unique identifiers
    /**The unique identifier of the entity.*/
    long id;
    /**The name of the entity.*/
    private String name;

    /**Default constructor for entity.*/
    Entity() {
        this.id = NEXT_ID.getAndIncrement();
    }

    /**Constructor for entity passed an ID for overwriting existing entities.
     * @param id The unique identifier of the entity.*/
    Entity(Long id) {
        this.id = id;
    }

    /**Gets the entity ID.
     * @return long The unique identifier of the entity.*/
    public long getId() {
        return id;
    }

    /**The name of the entity.
     * @return String The name of the entity.*/
    public String getName() {
        return name;
    }

    /**The unique identifier of any entity in a game.
     * @param name The name to set to the entity.*/
    public void setName(String name) {
        this.name = name;
    }

    /** Determines if the entity can hold additional entities within itself.
     * Default entities do not contain other entities; override to return true in subclasses that do.
     * @return boolean Returns true if item can hold additional entities. */
    public boolean containsEntities() {
        return false;
    }

    /**Determines that entities are equal if their unique IDs are equal.*/
    @Override
    public boolean equals(Object o) { //entity identified by its id
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
