import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Admin {

    public static void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int option = 0;

            System.out.println();
            System.out.println("------ ADMIN MENU ------");
            System.out.println("1. Add money");
            System.out.println("2. Deduct money");
            System.out.println("3. Transfer money");
            System.out.println("4. Add resources");
            System.out.println("5. Subtract resources");
            System.out.println("6. Server Shutdown");
            System.out.println("------------------------");
            System.out.println();
            System.out.print("Please enter your choice (1-6): ");

            // Leave when there is nothing to read from the standard input
            if (!scanner.hasNext()) {
                return;
            }

            // Attempt to read an integer
            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
            }

            // Discard any left over character
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            System.out.println();
            //execute a function based on the chosen option
            switch (option) {
                case 1 -> addMoney(scanner);
                case 2 -> deductMoney(scanner);
                case 3 -> transferMoney(scanner);
                case 4 -> addResources(scanner);
                case 5 -> subtractResources(scanner);
                case 6 -> serverShutdown();
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void serverShutdown() {

        if (!SocketHandler.getOnlineUsersMap().keySet().isEmpty()) {

            for (Account account : SocketHandler.getOnlineUsersMap().keySet()) {
                // Persist account balance
                CsvUtils.updateUserBalance(account);
                //Persist user inventory
                InventoryUtils.updateInventoryInCSV(account);
                Socket tempSocket = SocketHandler.getOnlineUsersMap().get(account);

                // Creates a temp PW that will write to recipient client
                try {
                    PrintWriter specificPW = new PrintWriter(tempSocket.getOutputStream(), true);
                    // Sends message to recipient client that informs of balance update
                    specificPW.println("SERVER_DISCONNECT");
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // persist marketplace
        CsvUtils.saveMarketItems(SocketHandler.marketplace.getInventory());
        System.out.println("Server shutting down...");
        System.exit(0);

    }

    //add money methods
    public static void addMoney(Scanner scanner) {
        System.out.print("Enter the username of the balance to increment: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String name = scanner.nextLine();

        Account user = null;
        //search for user by name
        for (Account account : SocketHandler.users) {
            if (account.getUserName().equals(name)) {
                user = account;
            }
        }

        if (user != null) {
            int amount = readAmount(scanner, "Enter amount to increment: ");

            user.incrementBalance(amount);

            System.out.println("\n" + user.getUserName() + "'s " + amount + "£ is added!");
        } else {
            System.out.println("User not found");
        }
    }

    public static void deductMoney(Scanner scanner) {
        System.out.print("Enter the username of the balance to decrement: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String name = scanner.nextLine();

        Account user = null;

        for (Account account : SocketHandler.users) {
            if (account.getUserName().equals(name)) {
                user = account;
            }
        }

        if (user != null) {
            int amount = readAmount(scanner, "Enter amount to decrement: ");

            user.decrementBalance(amount);

            System.out.println("\n" + user.getUserName() + "'s £" + amount + "£ is decremented!");
        } else {
            System.out.println("User not found");
        }
    }

    //transfer money using the sender and the recipient.
    public static void transferMoney(Scanner scanner) {
        System.out.print("Enter the username of sender: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String senderName = scanner.nextLine();

        Account sender = null;
        //search the sender by name
        for (Account account : SocketHandler.users) {
            if (account.getUserName().equals(senderName)) {
                sender = account;
            }
        }

        if (sender == null) {
            System.out.println("Sender not found");
            return;
        }

        System.out.print("Enter the username of recipient: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String recipientName = scanner.nextLine();

        Account recipient = null;
        //search the recipient by name
        for (Account account : SocketHandler.users) {
            if (account.getUserName().equals(recipientName)) {
                recipient = account;
            }
        }

        if (recipient == null) {
            System.out.println("Recipient not found");
            return;
        }

        int amount = readAmount(scanner, "Enter amount to transfer: ");

        if (sender.transferBalance(amount, recipient)) {
            System.out.println("\n" + senderName + " transferred £" + amount + " to " + recipientName);
        }
    }

    public static void addResources(Scanner scanner) {
        System.out.print("Enter the name of the item you want to add: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String itemName = scanner.nextLine();

        int quantity = readAmount(scanner, "Enter the amount you want to add: ");

        System.out.println();
        if (SocketHandler.marketplace.addToInventory(itemName, quantity)) {
            System.out.println("\n" + " " + quantity + " items of " + itemName + " are added!");
        }
    }

    public static void subtractResources(Scanner scanner) {
        System.out.print("Enter the name of the item you want to subtract: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String itemName = scanner.nextLine();

        int quantity = readAmount(scanner, "Enter the amount you want to subtract: ");

        System.out.println();

        if (SocketHandler.marketplace.removeFromInventory(itemName, quantity)) {
            System.out.println("\n" + quantity + itemName + " are removed!");
        }
    }

    //receive an integer amount from the user
    private static int readAmount(Scanner scanner, String text) {
        int quantity = 0;

        while (true) {
            System.out.print(text);
            //skip if there is no more input to process
            if (!scanner.hasNext()) {
                break;
            }
            //read an integer
            if (scanner.hasNextInt()) {
                quantity = scanner.nextInt();
            }
            //discard the remaining characters
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            if (quantity > 0) {
                break;
            }

            System.out.println("\nInvalid value.\n");
        }

        return quantity;
    }

}
