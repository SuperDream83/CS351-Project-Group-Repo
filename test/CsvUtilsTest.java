import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class CsvUtilsTest {
    private static File testAccountsFile = new File("test/Resources/test_accounts.csv");
    private static File testMarketFile = new File("test/Resources/test_market.csv");

    @BeforeEach
    public void setUp() throws IOException {
        // Create a CSV file with headers and sample data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testAccountsFile))) {
            writer.write("username,password,balance\n"); // headers
            writer.write("user1,pass123,1000\n");
            writer.write("user2,pass123,1000\n");
            writer.write("user3,pass123,1000\n");
        }
    }


    @Test
    public void testMarketplaceFileLoad() {
        List<MarketItem> inventory = new ArrayList<>();
        CsvUtils.loadMarketItems(inventory, testMarketFile);

        assertEquals(inventory.size(), 5);
        assertEquals(inventory.get(0).getItem(), "Wood");
        assertEquals(inventory.get(1).getItem(), "Stone");
        assertEquals(inventory.get(2).getItem(), "Iron");
        assertEquals(inventory.get(3).getItem(), "Gold");
        assertEquals(inventory.get(4).getItem(), "Silver");
    }

//    @Test
//    public void testMarketFileUpdates() {
//        List<MarketItem> inventory = new ArrayList<>();
//        CsvUtils.loadMarketItems(inventory, testMarketFile);
//
//        Account acc1 = new Account("user1", "pass123", 1000);
//    }

    @Test
    public void testAccountsFileLoad() {
        List<Account> users = new ArrayList<>();

        users = CsvUtils.getAccounts(testAccountsFile);

        assertEquals(users.size(), 3);
        assertEquals(users.get(0).getUserName(), "user1");
        assertEquals(users.get(1).getUserName(), "user2");
        assertEquals(users.get(2).getUserName(), "user3");
    }

    @Test
    public void testUserRegisteredToCSV() {
        Account accountToAdd = new Account("user4", "pass123", 1000);

        assertEquals(CsvUtils.getAccounts(testAccountsFile).size(), 3);
        CsvUtils.saveAccountToCSV(accountToAdd, testAccountsFile);
        assertEquals(CsvUtils.getAccounts(testAccountsFile).size(), 4);
        assertEquals(CsvUtils.getAccounts(testAccountsFile).get(3).getUserName(), "user4");

    }
}
