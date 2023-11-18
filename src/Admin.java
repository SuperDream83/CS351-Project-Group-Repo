import java.util.Scanner;

public class Admin {

    public static void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int option = 0;

            while (true) {
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

                if (option > 0 && option < 6) {
                    break;
                }

                System.out.println();
                System.out.println("Invalid option, please try again.");
            }

            System.out.println();

            switch (option) {
                case 1 -> {
                    System.out.println("option 1");
                }
                case 2 -> {
                    System.out.println("option 2");
                }
                case 3 -> {
                    System.out.println("option 3");
                }
                case 4 -> addResources(scanner);
                case 5 -> subtractResources(scanner);
            }
        }
    }

    public static void addResources(Scanner scanner) {
        System.out.print("Enter the name of the item you want to add: ");
        String itemName = scanner.nextLine();

        int quantity = 0;

        while (true) {
            System.out.print("Enter the quantity you want to add: ");

            if (scanner.hasNextInt()) {
                quantity = scanner.nextInt();
            }

            scanner.nextLine();

            if (quantity > 0) {
                break;
            }

            System.out.println("\nInvalid value.\n");
        }

        System.out.println();
        SocketHandler.marketplace.addToInventory(itemName, quantity);
    }

    public static void subtractResources(Scanner scanner) {
        System.out.print("Enter the name of the item you want to subtract: ");
        String itemName = scanner.nextLine();

        int quantity = 0;

        while (true) {
            System.out.print("Enter the quantity you want to subtract: ");

            if (scanner.hasNextInt()) {
                quantity = scanner.nextInt();
            }

            scanner.nextLine();

            if (quantity > 0) {
                break;
            }

            System.out.println("\nInvalid value.\n");
        }

        System.out.println();
        SocketHandler.marketplace.removeFromInventory(itemName, quantity);
    }

}
