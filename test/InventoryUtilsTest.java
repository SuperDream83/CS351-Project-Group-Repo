import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryUtilsTest {

    private static File testUsersInventoryFile = new File("test/Resources/test_usersInventory.csv");
    private static File testAccountsFile = new File("test/Resources/test_accounts.csv");


    @BeforeEach
    public void setUp() {
        // Create a CSV file with headers and sample data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testUsersInventoryFile))) {
            writer.write("username,item,quantity\n"); // headers
            writer.write("user1,Iron,10\n");
            writer.write("user3,Silver,5\n");
            writer.write("user2,Gold,2\n");
            writer.write("user3,Iron,15\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetUsersInventory() {
        Account user1 = new Account("user1", "pass123", 1000);
        Account user2 = new Account("user2", "pass123", 1000);
        Account user3 = new Account("user3", "pass123", 1000);
        InventoryUtils.readInventoryForAccount(user1, testUsersInventoryFile);
        InventoryUtils.readInventoryForAccount(user2, testUsersInventoryFile);
        InventoryUtils.readInventoryForAccount(user3, testUsersInventoryFile);
        Map<String, Integer> user1Inventory = user1.getInventory();
        Map<String, Integer> user2Inventory = user2.getInventory();
        Map<String, Integer> user3Inventory = user3.getInventory();
        assertNotNull(user1Inventory.get("Iron"));
        assertEquals(user1Inventory.get("Iron"), 10);
        assertNotNull(user2Inventory.get("Gold"));
        assertEquals(user2Inventory.get("Gold"), 2);
        assertNotNull(user3Inventory.get("Silver"));
        assertEquals(user3Inventory.get("Silver"), 5);
        assertNotNull(user3Inventory.get("Iron"));
        assertEquals(user3Inventory.get("Iron"), 15);
    }

    @Test
    public void testUpdatedUserInventorySaves() {
        List<Account> accounts = CsvUtils.getAccounts(testAccountsFile);
        Account user = accounts.get(0);

        InventoryUtils.readInventoryForAccount(user, testUsersInventoryFile);

        // Add Gold to inventory and save it to the file.
        user.addToInventory("Gold", 10);
        InventoryUtils.updateInventoryInCSV(user, testUsersInventoryFile);

        // Get the inventory from the file after it's been updated.
        accounts = CsvUtils.getAccounts(testAccountsFile);
        user = accounts.get(0);

        InventoryUtils.readInventoryForAccount(user, testUsersInventoryFile);

        assertNotNull(user.getInventory().get("Gold"));
        assertEquals(user.getInventory().get("Gold"), 10);
    }
}
