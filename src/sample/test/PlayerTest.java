package sample.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import sample.game.Direction;
import sample.game.Item;
import sample.game.Player;
import sample.game.Room;
import sample.generator.IllegalRoomConnection;

public class PlayerTest {
    Player player;
    Room room = new Room();
    @BeforeEach
    void setUp() {
        player = new Player("player");
        room = new Room();
        player.setCurrentRoom(room);
    }
    @Test
    public void playerAcquireItem_givenItem_thenReturnInventory() {
        Item item = new Item();
        room.addItem(item);
        player.acquire(item.getName());
        Assertions.assertTrue(player.getInventory().containsItem(item));
    }

    @Test
    public void playerDropItem_hasItem_thenReturnInventoryWithoutItem() {
        Item item = new Item();
        player.getInventory().addItem(item);
        player.drop(item.getName());
        Assertions.assertFalse(player.getInventory().containsItem(item));
    }

    @Test
    public void playerTravel_givenNewRoomExists_thenCurrentRoomIsNewRoom() throws IllegalRoomConnection {
        Room room2 = new Room();
        room.addExit(Direction.NORTH, room2);
        player.travel(Direction.NORTH);
        Assertions.assertSame(player.getCurrentRoom(), room2);
    }

    @Test
    public void playerTravel_givenNoNewRoom_thenNoTravel() {
        player.travel(Direction.NORTH);
        Assertions.assertSame(player.getCurrentRoom(), room);
    }

    @Test
    public void playerTakeDamage_givenDamage_thenReturnHp() {
        int expectedHp = player.getMaxHp() - 10;
        player.giveHp(-10);
        int actualHp = player.getCurrentHp();
        Assertions.assertEquals(expectedHp, actualHp);
    }

    @Test
    public void playerTakeDamage_givenDamage_capAtZero() {
        int expectedHp = 0;
        player.giveHp((player.getMaxHp() + 10) * -1);
        int actualHp = player.getCurrentHp();
        Assertions.assertEquals(expectedHp, actualHp);
    }

    @Test
    public void playerHeal_givenHealth_capAtMaxHealth() {
        int expectedHp = player.getMaxHp();
        player.giveHp(50);
        int actualHp = player.getCurrentHp();
        Assertions.assertEquals(expectedHp, actualHp);
    }
}
