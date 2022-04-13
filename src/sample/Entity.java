package sample;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Entity {
    static final AtomicLong NEXT_ID = new AtomicLong(0);
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

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
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
