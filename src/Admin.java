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
            System.out.println("------------------------");
            System.out.println();
            System.out.print("Please enter your choice (1-5): ");

            if (!scanner.hasNext()) {
                return;
            }

            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
            }

            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            System.out.println();

            switch (option) {
                case 1 -> addMoney(scanner);
                case 2 -> deductMoney(scanner);
                case 3 -> transferMoney(scanner);
                case 4 -> addResources(scanner);
                case 5 -> subtractResources(scanner);
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }

    public static void addMoney(Scanner scanner) {
        System.out.print("Enter the user name of the balance to increment: ");
        String name = scanner.nextLine();

        Account user = null;

        for (Account account : SocketHandler.users) {
            if (account.getUserName().equals(name)) {
                user = account;
            }
        }

        if (user != null) {
            int amount = readAmount(scanner, "Enter amount to increment: ");

            user.incrementBalance(amount);
        } else {
            System.out.println("User not found");
        }
    }

    public static void deductMoney(Scanner scanner) {
        System.out.print("Enter the user name of the balance to decrement: ");
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
        } else {
            System.out.println("User not found");
        }
    }

    public static void transferMoney(Scanner scanner) {
        System.out.print("Enter the user name of sender: ");
        String senderName = scanner.nextLine().trim();

        Account sender = null;

        for (Account account : SocketHandler.users) {
            if (account.getUserName().equals(senderName)) {
                sender = account;
            }
        }

        if (sender == null) {
            System.out.println("Sender not found");
            return;
        }

        System.out.print("Enter the user name of recipient: ");
        String recipientName = scanner.nextLine().trim();

        Account recipient = null;

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

        sender.transferBalance(amount, recipient);
    }

    public static void addResources(Scanner scanner) {
        System.out.print("Enter the name of the item you want to add: ");
        String itemName = scanner.nextLine();

        int quantity = readAmount(scanner, "Enter the amount you want to add: ");

        System.out.println();
        SocketHandler.marketplace.addToInventory(itemName, quantity);
    }

    public static void subtractResources(Scanner scanner) {
        System.out.print("Enter the name of the item you want to subtract: ");
        String itemName = scanner.nextLine();
        int quantity = readAmount(scanner, "Enter the amount you want to subtract: ");

        System.out.println();
        SocketHandler.marketplace.removeFromInventory(itemName, quantity);
    }

    private static int readAmount(Scanner scanner, String text) {
        int quantity = 0;

        while (true) {
            System.out.print(text);

            if (!scanner.hasNext()) {
                break;
            }

            if (scanner.hasNextInt()) {
                quantity = scanner.nextInt();
            }

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
