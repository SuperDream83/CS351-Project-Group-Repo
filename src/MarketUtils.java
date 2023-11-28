import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class MarketUtils {

    public static Marketplace marketplace;
    private static Scanner scanner = new Scanner(System.in);

    public MarketUtils(Marketplace marketplace) {
        MarketUtils.marketplace = marketplace;

    }

    public static void marketplaceMenu(PrintWriter printWriter, BufferedReader in, Account userAccount,
                                       BlockingQueue<Runnable> taskQueue) {
        boolean continueMarketplace = true;

        while (continueMarketplace) {
            System.out.println("--------- MARKETPLACE MENU ---------");
            System.out.println("1. View listings");
            System.out.println("2. Buy items");
            System.out.println("3. Sell items");
            System.out.println("4. Main Menu");
            System.out.print("Please enter your choice (1-4): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    printWriter.println("VIEW_MARKETPLACE");
                    try {
                        Runnable viewMarketplace = taskQueue.take();
                        viewMarketplace.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "2":
                    buyItems(userAccount, printWriter, in);
                    try {
                        Runnable buyItems = taskQueue.take();
                        buyItems.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    break;
                case "3":
                    sellItems(userAccount, printWriter, in, taskQueue);
                    try {
                        Runnable sellItems = taskQueue.take();
                        sellItems.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "4":
                    continueMarketplace = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select a number between 1 and 4.");
                    break;
            }
        }
    }

    public static void buyItems(Account userAccount, PrintWriter printWriter, BufferedReader in) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the item you want to buy: ");
        String itemName = scanner.nextLine();

        int quantity;

        do {
            System.out.print("Enter quantity you wish to send: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Please enter an integer value: ");
                scanner.next(); // Read and discard the non-integer input
            }
            quantity = scanner.nextInt();
            scanner.nextLine(); // Consume the newline left-over

            if (quantity <= 0) {
                System.out.print("Please enter a positive integer value: ");
            }

        } while (quantity <= 0);

        printWriter.println("BUY_ITEM" + "|" + itemName + "|" + quantity);

    }

    public static void sellItems(Account userAccount, PrintWriter printWriter, BufferedReader in,
                                 BlockingQueue<Runnable> taskQueue) {
        Scanner scanner = new Scanner(System.in);
        // Display the user's inventory
        printWriter.println("VIEW_USER_ACCOUNT");
        try {
            Runnable viewCurrentInv = taskQueue.take();
            viewCurrentInv.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        printWriter.flush();

        System.out.print("Enter the name of the item you want to sell: ");
        String itemName = scanner.nextLine();


        int quantityToSell;

        do {
            System.out.print("Enter quantity you wish to send: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Please enter an integer value: ");
                scanner.next(); // Read and discard the non-integer input
            }
            quantityToSell = scanner.nextInt();
            scanner.nextLine(); // Consume the newline left-over

            if (quantityToSell <= 0) {
                System.out.print("Please enter a positive integer value: ");
            }

        } while (quantityToSell <= 0);

        printWriter.println("SELL_ITEM" + "|" + itemName + "|" + quantityToSell);

    }
}
