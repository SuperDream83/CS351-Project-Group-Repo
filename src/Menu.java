import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Menu {

    private Scanner scanner;

    private Account account;

    private PrintWriter printWriter;
    private BufferedReader in;

    public Menu(BufferedReader bufferedReader, PrintWriter printWriter) {
        scanner = new Scanner(System.in);
        this.printWriter = printWriter;
        this.in = bufferedReader;
    }

    public void displayMenu(BlockingQueue<Runnable> taskQueue) {

        while (account == null) {
            System.out.println("--------- WELCOME ---------");
            System.out.println("1. Log in");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.println("------------------------");
            System.out.print("Please enter your choice (1-3): ");

            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> {
                        // Logic for login
                        System.out.println("You selected: Log in");
                        AccountUtils.login(printWriter);
                        try {
                            Runnable receiveLoginFromServer = taskQueue.take();
                            receiveLoginFromServer.run();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    case 2 -> {
                        // Logic for registering a new user
                        System.out.println("You selected: Register");
                        AccountUtils.register(printWriter);
                        try {
                            Runnable receiveRegisterFromServer = taskQueue.take();
                            receiveRegisterFromServer.run();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    case 3 -> {
                        System.out.println("Goodbye!");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice. Please select a number between 1 and 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 3.");
                scanner.next(); // clear the invalid input
            }

        }

        while (true) {
            System.out.println("--------- MENU ---------");
            System.out.println("1. View my inventory");
            System.out.println("2. View logged on users");
            System.out.println("3. View Marketplace");
            System.out.println("4. Quit");
            System.out.println("------------------------");
            System.out.print("Please enter your choice (1-4): ");


            try {
                int choice = scanner.nextInt();
                handleMenuChoice(choice, taskQueue);
                if (choice == 4) {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scanner.next(); // clear the invalid input
            }
        }

    }

    // CHOICE HANDLER
    private void handleMenuChoice(int choice, BlockingQueue<Runnable> taskQueue) {
        switch (choice) {
            case 1:
                // Logic for viewing inventory
                System.out.println("You selected: View my inventory");
                printWriter.println("VIEW_USER_ACCOUNT");
                try {
                    Runnable viewUserInv = taskQueue.take();
                    viewUserInv.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                // Logic for viewing logged on users
                System.out.println("You selected: View logged on users");
                AccountUtils.accountMenu(printWriter, account, taskQueue);
                break;
            case 3:
                // Logic for viewing the marketplace
                System.out.println("You selected: View Marketplace");
                MarketUtils.marketplaceMenu(printWriter, in, account, taskQueue);
                break;
            case 4:
                printWriter.println("LOG_OFF" + "|" + account.getUserName());
                System.out.println("Goodbye!");
                break;
            default:
                System.out.println("Invalid choice. Please select a number between 1 and 4.");
                break;
        }

    }

    public void setAccount(Account account) {
        this.account = account;
    }


}
