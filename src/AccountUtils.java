import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class AccountUtils {

    private static Scanner scanner = new Scanner(System.in);

    public AccountUtils() {

    }

    public static int login(PrintWriter printWriter) {
        System.out.print("Enter username (or 'exit' to cancel): ");
        String username = scanner.nextLine();

        if (username.equalsIgnoreCase("exit")) {
            return 0; // User chose to exit
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        printWriter.println("LOGIN" + "|" + username + "|" + password);
        return 1;
    }

    public static int register(PrintWriter printWriter) {
        System.out.print("Enter username (or 'exit' to cancel): ");
        String username = scanner.nextLine();

        if (username.equalsIgnoreCase("exit")) {
            return 0; // User chose to exit
        }

        String password = "";
        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();

            System.out.print("Enter password again to confirm: ");
            String passwordConfirm = scanner.nextLine();
            if (password.equals(passwordConfirm)) {
                break;
            }
            System.out.println("Password Mismatch, please try again!");
        }

        printWriter.println("REGISTER" + "|" + username + "|" + password);
        return 1;


    }

    public static void accountMenu(PrintWriter printWriter, Account account,
                                   BlockingQueue<Runnable> taskQueue) {
        boolean continueAccount = true;

        while (continueAccount) {
            System.out.println("--------- ACCOUNT MENU ---------");
            System.out.println("1. View online accounts");
            System.out.println("2. Transfer balance to user");
            System.out.println("3. Main Menu");
            System.out.print("Please enter your choice (1-3): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    printWriter.println("VIEW_ONLINE_USERS");
                    try {
                        Runnable viewOnlineUsers = taskQueue.take();
                        viewOnlineUsers.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "2":
                    sendMoneyToUsers(printWriter, account);
                    try {
                        Runnable sendMoneyToUsers = taskQueue.take();
                        sendMoneyToUsers.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "3":
                    continueAccount = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select a number between 1 and 3.");
                    break;
            }
        }
    }

    private static void sendMoneyToUsers(PrintWriter printWriter, Account account) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("----Send money to user----");
        System.out.print("Enter user you wish to send money too: ");
        String recipient = scanner.nextLine().trim();

      //  scanner.nextLine();

        int amount;

        do {
            System.out.print("Enter amount you wish to send: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Please enter an integer value: ");
                scanner.next(); // Read and discard the non-integer input
            }
            amount = scanner.nextInt();
            scanner.nextLine(); // Consume the newline left-over

            if (amount <= 0) {
                System.out.print("Please enter a positive integer value: ");
            }

        } while (amount <= 0);

        printWriter.println("SEND_MONEY_TO_USER" + "|" + recipient + "|" + amount + "|" + account.getUserName());

    }
}
