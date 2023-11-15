import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    ServerSocket serverSocket;
    ExecutorService threadpool;

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(11000);
        threadpool = Executors.newFixedThreadPool(20);
    }

    @Override
    public void run() {
        // Want a command that can shutdown the server
        try {
            System.out.println("Server Running");
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected");
                // Create socketHander
                // Pass to threadpool
                threadpool.submit(new SocketHandler(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            Thread thread = new Thread(server);
            thread.start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}

