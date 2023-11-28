import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CsvUtilsTest {
    private static final File testAccountsFile = new File("test/Resources/test_accounts.csv");
    private static final File testMarketFile = new File("test/Resources/test_market.csv");

    @BeforeEach
    public void setUp() throws IOException {
        // Create a CSV file with headers and sample data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testAccountsFile))) {
            writer.write("username,password,balance\n"); // headers
            writer.write("user1,pass123,1000\n");
            writer.write("user2,pass123,1000\n");
            writer.write("user3,pass123,1000\n");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testMarketFile))) {
            writer.write("item,quantity,buyPrice,sellPrice\n"); // headers
            writer.write("Wood,50,10,8\n");
            writer.write("Stone,100,10,8\n");
            writer.write("Iron,30,30,24\n");
            writer.write("Gold,5,100,80\n");
            writer.write("Silver,20,50,40\n");
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

    @Test
    public void testMarketFileUpdates() {
        Marketplace marketplace = new Marketplace(testMarketFile);
        Assertions.assertEquals(marketplace.findItem("Gold").getQuantity(), 5);

        Account user = CsvUtils.getAccounts(testAccountsFile).get(0);
        Map<String, Integer> userInventory = user.getInventory();

        MarketItem item = new MarketItem("Gold", 15, 100, 80);
        marketplace.addToInventory(item.getItem(), item.getQuantity());

        CsvUtils.saveMarketItems(marketplace.getInventory(), testMarketFile);

        Marketplace updatedMarket = new Marketplace(testMarketFile);
        Assertions.assertEquals(updatedMarket.findItem("Gold").getQuantity(), 20);
    }

    @Test
    public void testAccountsFileLoad() {
        List<Account> users = new ArrayList<>();

        users = CsvUtils.getAccounts(testAccountsFile);

        Assertions.assertEquals(users.size(), 3);
        Assertions.assertEquals(users.get(0).getUserName(), "user1");
        Assertions.assertEquals(users.get(1).getUserName(), "user2");
        Assertions.assertEquals(users.get(2).getUserName(), "user3");
    }

    @Test
    public void testUserRegisteredToCSV() {
        Account accountToAdd = new Account("user4", "pass123", 1000);

        Assertions.assertEquals(CsvUtils.getAccounts(testAccountsFile).size(), 3);
        CsvUtils.saveAccountToCSV(accountToAdd, testAccountsFile);
        Assertions.assertEquals(CsvUtils.getAccounts(testAccountsFile).size(), 4);
        Assertions.assertEquals(CsvUtils.getAccounts(testAccountsFile).get(3).getUserName(), "user4");

    }
}
