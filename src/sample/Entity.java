package sample;

import java.util.concurrent.atomic.AtomicLong;

public class Entity {
    static final AtomicLong NEXT_ID = new AtomicLong(0);
    long id;

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
}
