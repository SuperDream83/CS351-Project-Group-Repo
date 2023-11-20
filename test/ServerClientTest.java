import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertFalse;

public class ServerClientTest {

    boolean serverError;

    boolean clientError;


    @Test
    public void executionTest() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                Server.main(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
                serverError = true;
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Client.main(new String[0]);
            } catch (IOException e) {
                e.printStackTrace();
                clientError = true;
            }
        });

        t1.start();
        t2.start();

        Thread.sleep(1000);

        assertFalse(serverError);
        assertFalse(clientError);
    }

}
