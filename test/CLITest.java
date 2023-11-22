import org.junit.After;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class CLITest {

    // https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println
    // array of bytes used as buffer for the console output
    ByteArrayOutputStream outContent;
    InputStream originalIn = System.in;
    PrintStream originalOut = System.out;
    //PrintStream used to capture the console output
    PrintStream tempOut;

    //retrieve the captured console output
    public String getSystemOut() {
        return outContent.toString();
    }

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

    //restore the original input and output stream
    @After
    public void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

}
