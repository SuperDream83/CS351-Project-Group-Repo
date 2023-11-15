import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class AccountUtils {

    private static Scanner scanner = new Scanner(System.in);

    public AccountUtils() {

    }

    public static Account login(PrintWriter printWriter, BufferedReader in) {
        while (true) {
            System.out.print("Enter username (or 'exit' to cancel): ");
            String username = scanner.nextLine();

            if (username.equalsIgnoreCase("exit")) {
                return null; // User chose to exit
            }

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            printWriter.println("LOGIN" + "|" + username + "|" + password);

            try {
                String output = in.readLine();

                if (output.equals("SUCCESSFUL_LOGON")){
                    // Balance is 0 on client side as actual balance is kept on online user list on server
                    return new Account(username, password, 0);
                } else {
                    System.out.println("Login Unsuccessful - Please try again!");
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static Account register(PrintWriter printWriter, BufferedReader in) {
        while (true) {
            System.out.print("Enter username (or 'exit' to cancel): ");
            String username = scanner.nextLine();

            if (username.equalsIgnoreCase("exit")) {
                return null; // User chose to exit
            }

            String password = "";
            while (true) {
                System.out.print("Enter password: ");
                password = scanner.nextLine();

                System.out.print("Enter password again to confirm: ");
                String passwordConfirm = scanner.nextLine();
                if (password.equals(passwordConfirm)){
                    break;
                }
                System.out.println("Password Mismatch, please try again!");
            }

            printWriter.println("REGISTER" + "|" + username + "|" + password);

            try {
                String output = in.readLine();

                if (output.contains("Account Created")){
                    // Balance is 0 on client side as actual balance is kept on online user list on server
                    System.out.println(output);
                    return new Account(username, password, 0);
                } else {
                    System.out.println(output);
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void accountMenu(PrintWriter printWriter, BufferedReader in, Account account) {
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
                        String output = in.readLine();
                        String[] splitOut = output.split("-");
                        for (String line : splitOut) {
                            System.out.println(line);
                        }
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "2":
                    sendMoneyToUsers(printWriter, in, account);
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

    private static void sendMoneyToUsers(PrintWriter printWriter, BufferedReader in, Account account) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("----Send money to user----");
        System.out.print("Enter user you wish to send money too: ");
        String recipient = scanner.nextLine().trim();
        System.out.print("Enter amount you wish to send: ");
        String amount = scanner.nextLine().trim();

        printWriter.println("SEND_MONEY_TO_USER" + "|" + recipient + "|" + amount + "|" + account.getUserName());

        try {
            String output = in.readLine();
            System.out.println(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
