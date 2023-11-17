import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class AdminTest {

    // https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println

    private ByteArrayOutputStream outContent;
    private InputStream originalIn = System.in;
    private PrintStream originalOut = System.out;
    private PrintStream tempOut;

    public void setIn(String text) {
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
        setIn("1\n");

        Admin.run();

        String out = outContent.toString();

        assertTrue(out.contains("ADMIN MENU"));
        assertTrue(out.contains("option 1"));
    }

}
