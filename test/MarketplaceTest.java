import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MarketplaceTest {

    @Test
    public void testAddAndRemoveItems() throws InterruptedException {
        Marketplace marketplace = new Marketplace();

        MarketItem iron = marketplace.findItem("Iron");

        assertEquals(30, iron.getQuantity());

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                marketplace.addToInventory("Iron", 10);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                marketplace.removeFromInventory("Iron", 1);
            }
        });

        t1.start();
        t2.start();

        // https://ducmanhphan.github.io/2020-03-20-Waiting-threads-to-finish-completely-in-Java/
        t1.join(); // wait for thread t1's completion
        t2.join(); // wait for thread t2's completion

        assertEquals(9030, iron.getQuantity());
    }

}
