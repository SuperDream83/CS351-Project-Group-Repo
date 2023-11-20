import org.junit.After;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class CliTest {

    // https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println
    // array of bytes used as buffer
    ByteArrayOutputStream outContent;
    InputStream originalIn = System.in;
    PrintStream originalOut = System.out;
    PrintStream tempOut;

    // prepare the fake input stream
    public void setSystemIn(String text) {
        System.setIn(new ByteArrayInputStream(text.getBytes()));
    }

    // prepare the fake output stream
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

}
