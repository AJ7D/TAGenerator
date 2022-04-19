package sample;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Entity implements Serializable {
    private static final long serialVersionUID = 1L;
    static final AtomicLong NEXT_ID = new AtomicLong(0); //generate unique identifiers
    long id;
    private String name;

    Entity() {
        this.id = NEXT_ID.getAndIncrement();
    }

    Entity(Long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
