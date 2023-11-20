import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdminTest {

    // https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println

    private ByteArrayOutputStream outContent;
    private InputStream originalIn = System.in;
    private PrintStream originalOut = System.out;
    private PrintStream tempOut;

    public void setSystemIn(String text) {
        System.setIn(new ByteArrayInputStream(text.getBytes()));
    }

    @Before
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        tempOut = new PrintStream(outContent, true);

        System.setOut(tempOut);
    }

    @After
    public void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void testAddMoney() {
        setSystemIn("1\nmissing\n1\nKeti\n100\n");

        Account account = SocketHandler.users.get(4);

        account.setBalance(10);

        Admin.run();

        String out = outContent.toString();

        System.err.println(out);

        assertTrue(out.contains("ADMIN MENU"));
        assertTrue(out.contains("User not found"));
        assertEquals( Integer.valueOf(110), account.getBalance());
    }

    @Test
    public void testRemoveMoney() {
        setSystemIn("2\nmissing\n2\nKeti\n20\n2\nKeti\n5\n");

        Account account = SocketHandler.users.get(4);

        account.setBalance(10);

        Admin.run();

        String out = outContent.toString();

        System.err.println(out);

        assertTrue(out.contains("ADMIN MENU"));
        assertTrue(out.contains("User not found"));
        assertTrue(out.contains("Cannot decrement amount, there is no enough balance"));
        assertEquals(Integer.valueOf(5), account.getBalance());
    }

    @Test
    public void testTransferMoney() {
        setSystemIn("3\nmissing\n3\nKeti\nmissing\n3\nKeti\nGreg\n20\n3\nKeti\nGreg\n5\n");

        Account sender = SocketHandler.users.get(4);
        Account recipient = SocketHandler.users.get(5);

        sender.setBalance(10);
        recipient.setBalance(10);

        Admin.run();

        String out = outContent.toString();

        System.err.println(out);

        assertTrue(out.contains("ADMIN MENU"));
        assertTrue(out.contains("Sender not found"));
        assertTrue(out.contains("Recipient not found"));
        assertTrue(out.contains("Cannot decrement amount, there is no enough balance"));
        assertEquals(Integer.valueOf(5), sender.getBalance());
        assertEquals(Integer.valueOf(15), recipient.getBalance());
    }

    @Test
    public void testAddResources() {
        setSystemIn("4\nmissing\n100\n4\nIron\n100\n");

        MarketItem iron = SocketHandler.marketplace.getInventory().get(2);

        iron.setQuantity(100);

        Admin.run();

        String out = outContent.toString();

        System.err.println(out);

        assertTrue(out.contains("ADMIN MENU"));
        assertTrue(out.contains("Item not found"));
        assertEquals(200, iron.getQuantity());
    }

    @Test
    public void testRemoveResources() {
        setSystemIn("5\nmissing\n1000\n5\nIron\n1000\n5\nIron\n100\n");

        MarketItem iron = SocketHandler.marketplace.getInventory().get(2);

        iron.setQuantity(200);

        Admin.run();

        String out = outContent.toString();

        System.err.println(out);

        assertTrue(out.contains("ADMIN MENU"));
        assertTrue(out.contains("Item not found"));
        assertTrue(out.contains("There is no enough quantity to subtract."));
        assertEquals(100, iron.getQuantity());
    }

}
