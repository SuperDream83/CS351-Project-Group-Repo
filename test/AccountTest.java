import org.junit.Test;

import static java.lang.Integer.valueOf;
import static org.junit.Assert.assertEquals;

public class AccountTest {

    @Test
    public void balanceUpdateTest() throws InterruptedException {
        Account sender = new Account("SENDER", null, 1000);
        Account recipient = new Account("RECIPIENT", null, 1000);

        assertEquals(valueOf(1000), sender.getBalance());
        assertEquals(valueOf(1000), recipient.getBalance());

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                sender.incrementBalance(10);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                sender.decrementBalance(1);
            }
        });

        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                sender.transferBalance(1, recipient);
            }
        });

        Thread t4 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                recipient.incrementBalance(1);
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        // https://ducmanhphan.github.io/2020-03-20-Waiting-threads-to-finish-completely-in-Java/
        t1.join(); // wait for thread t1's completion
        t2.join(); // wait for thread t2's completion
        t3.join(); // wait for thread t3's completion
        t4.join(); // wait for thread t4's completion

        assertEquals(valueOf(9000), sender.getBalance());
        assertEquals(valueOf(3000), recipient.getBalance());
    }

}
