import java.util.InputMismatchException;
import java.util.NoSuchElementException;
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

                try {
                    option = scanner.nextInt();

                    scanner.nextLine();

                    if (option > 0 && option < 6) {
                        break;
                    }
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                } catch(NoSuchElementException e) {
                    return;
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
                case 4 -> {
                    System.out.println("option 4");
                }
                case 5 -> {
                    System.out.println("option 5");
                }
            }
        }
    }

}
