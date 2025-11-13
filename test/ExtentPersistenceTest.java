import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.LocalDateTime;

/**
 * Unit tests for Extent Persistence
 * Tests saving and loading class extents to/from files
 */
public class ExtentPersistenceTest {

    private static final String TEST_CUSTOMERS_FILE = "test_customers.dat";
    private static final String TEST_TABLES_FILE = "test_tables.dat";
    private static final String TEST_INGREDIENTS_FILE = "test_ingredients.dat";

    @BeforeEach
    void clearExtents() {
        Customer.clearExtent();
        Table.clearExtent();
        Ingredient.clearExtent();
    }

    @AfterEach
    void cleanupFiles() {
        // Clean up test files after each test using PersistenceConfig
        PersistenceConfig.deleteDataFile(TEST_CUSTOMERS_FILE);
        PersistenceConfig.deleteDataFile(TEST_TABLES_FILE);
        PersistenceConfig.deleteDataFile(TEST_INGREDIENTS_FILE);
    }

    @Test
    @DisplayName("Save and load extent successfully")
    void testSaveAndLoadExtent() throws Exception {
        Table t1 = new Table(1, 4, "Main Hall");
        Table t2 = new Table(2, 2, "Terrace");
        Table t3 = new Table(3, 6, "Garden");

        assertEquals(3, Table.getAllTables().size());

        // Save extent
        Table.saveExtent(TEST_TABLES_FILE);

        // Clear extent
        Table.clearExtent();
        assertEquals(0, Table.getAllTables().size());

        // Load extent
        boolean loaded = Table.loadExtent(TEST_TABLES_FILE);
        assertTrue(loaded, "Extent should load successfully");
        assertEquals(3, Table.getAllTables().size());
    }

    @Test
    @DisplayName("Data integrity after save/load cycle")
    void testDataIntegrityAfterSaveLoad() throws Exception {
        Table t1 = new Table(1, 4, "Main Hall");
        Table t2 = new Table(2, 2, "Terrace");

        // Save
        Table.saveExtent(TEST_TABLES_FILE);

        // Clear and reload
        Table.clearExtent();
        Table.loadExtent(TEST_TABLES_FILE);

        // Verify data integrity
        assertEquals(2, Table.getAllTables().size());

        Table loadedTable1 = Table.getAllTables().get(0);
        assertEquals(1, loadedTable1.getNumber());
        assertEquals(4, loadedTable1.getCapacity());
        assertEquals("Main Hall", loadedTable1.getSection());

        Table loadedTable2 = Table.getAllTables().get(1);
        assertEquals(2, loadedTable2.getNumber());
        assertEquals(2, loadedTable2.getCapacity());
        assertEquals("Terrace", loadedTable2.getSection());
    }

    @Test
    @DisplayName("Loading nonexistent file returns false and clears extent")
    void testLoadNonexistentFile() {
        new Table(1, 4, "Main Hall");
        assertEquals(1, Table.getAllTables().size());

        boolean loaded = Table.loadExtent("nonexistent_file_xyz.dat");
        assertFalse(loaded, "Loading nonexistent file should return false");
        assertEquals(0, Table.getAllTables().size(), "Extent should be cleared on failed load");
    }

    @Test
    @DisplayName("Customer extent persistence with complex data")
    void testCustomerPersistence() throws Exception {
        Customer c1 = new Customer("John", "Doe", "john@test.com",
            "+48111222333", LocalDateTime.now().minusYears(1));
        Customer c2 = new Customer("Jane", "Smith", "jane@test.com",
            "+48444555666", LocalDateTime.now().minusMonths(6));

        String originalName1 = c1.getName();
        String originalEmail1 = c1.getEmail();
        String originalName2 = c2.getName();
        String originalEmail2 = c2.getEmail();

        Customer.saveExtent(TEST_CUSTOMERS_FILE);
        Customer.clearExtent();
        assertTrue(Customer.loadExtent(TEST_CUSTOMERS_FILE));

        assertEquals(2, Customer.getAllCustomers().size());

        Customer loaded1 = Customer.getAllCustomers().get(0);
        assertEquals(originalName1, loaded1.getName());
        assertEquals(originalEmail1, loaded1.getEmail());

        Customer loaded2 = Customer.getAllCustomers().get(1);
        assertEquals(originalName2, loaded2.getName());
        assertEquals(originalEmail2, loaded2.getEmail());
    }

    @Test
    @DisplayName("Save empty extent")
    void testSaveEmptyExtent() throws Exception {
        assertEquals(0, Table.getAllTables().size());

        // Save empty extent
        Table.saveExtent(TEST_TABLES_FILE);

        // Add some data
        new Table(1, 4, "Main Hall");
        assertEquals(1, Table.getAllTables().size());

        // Load empty extent
        Table.loadExtent(TEST_TABLES_FILE);
        assertEquals(0, Table.getAllTables().size());
    }

    @Test
    @DisplayName("Multiple save/load cycles")
    void testMultipleSaveLoadCycles() throws Exception {
        // First cycle
        Table t1 = new Table(1, 4, "Main Hall");
        Table.saveExtent(TEST_TABLES_FILE);
        Table.clearExtent();
        Table.loadExtent(TEST_TABLES_FILE);
        assertEquals(1, Table.getAllTables().size());

        // Second cycle - add more data
        Table t2 = new Table(2, 2, "Terrace");
        Table.saveExtent(TEST_TABLES_FILE);
        Table.clearExtent();
        Table.loadExtent(TEST_TABLES_FILE);
        assertEquals(2, Table.getAllTables().size());

        // Third cycle - verify persistence
        Table.saveExtent(TEST_TABLES_FILE);
        Table.clearExtent();
        Table.loadExtent(TEST_TABLES_FILE);
        assertEquals(2, Table.getAllTables().size());
    }

    @Test
    @DisplayName("Overwriting existing file")
    void testOverwriteExistingFile() throws Exception {
        // First save
        Table t1 = new Table(1, 4, "Main Hall");
        Table.saveExtent(TEST_TABLES_FILE);

        // Clear and create different data
        Table.clearExtent();
        Table t2 = new Table(2, 6, "Garden");
        Table t3 = new Table(3, 8, "Balcony");

        // Overwrite file
        Table.saveExtent(TEST_TABLES_FILE);

        // Load and verify new data
        Table.clearExtent();
        Table.loadExtent(TEST_TABLES_FILE);

        assertEquals(2, Table.getAllTables().size());
        assertEquals(2, Table.getAllTables().get(0).getNumber());
        assertEquals(3, Table.getAllTables().get(1).getNumber());
    }

    @Test
    @DisplayName("Ingredient persistence with numeric values")
    void testIngredientPersistence() throws Exception {
        Ingredient ing1 = new Ingredient("Flour", "kg", 50.5, 20.0, 3.25);
        Ingredient ing2 = new Ingredient("Sugar", "kg", 15.75, 10.0, 2.50);

        Ingredient.saveExtent(TEST_INGREDIENTS_FILE);
        Ingredient.clearExtent();
        assertTrue(Ingredient.loadExtent(TEST_INGREDIENTS_FILE));

        assertEquals(2, Ingredient.getAllIngredients().size());

        Ingredient loaded1 = Ingredient.getAllIngredients().get(0);
        assertEquals("Flour", loaded1.getName());
        assertEquals(50.5, loaded1.getCurrentStock(), 0.01);
        assertEquals(20.0, loaded1.getReorderPoint(), 0.01);
        assertEquals(3.25, loaded1.getCostPerUnit(), 0.01);
    }

    @Test
    @DisplayName("Load extent preserves derived attributes")
    void testDerivedAttributesAfterLoad() throws Exception {
        Ingredient ing1 = new Ingredient("Salt", "kg", 5, 20, 1.5);
        Ingredient ing2 = new Ingredient("Pepper", "kg", 50, 20, 2.0);

        assertTrue(ing1.getNeedsReorder(), "Should need reorder before save");
        assertFalse(ing2.getNeedsReorder(), "Should not need reorder before save");

        Ingredient.saveExtent(TEST_INGREDIENTS_FILE);
        Ingredient.clearExtent();
        Ingredient.loadExtent(TEST_INGREDIENTS_FILE);

        Ingredient loaded1 = Ingredient.getAllIngredients().get(0);
        Ingredient loaded2 = Ingredient.getAllIngredients().get(1);

        assertTrue(loaded1.getNeedsReorder(), "Derived attribute should work after load");
        assertFalse(loaded2.getNeedsReorder(), "Derived attribute should work after load");
    }

    @Test
    @DisplayName("File creation verification")
    void testFileCreation() throws Exception {
        Table t1 = new Table(1, 4, "Main Hall");
        Table.saveExtent(TEST_TABLES_FILE);

        File file = new File(PersistenceConfig.getDataFilePath(TEST_TABLES_FILE));
        assertTrue(file.exists(), "File should be created in data directory");
        assertTrue(file.length() > 0, "File should not be empty");
    }

    @Test
    @DisplayName("Extent persistence with special characters")
    void testSpecialCharactersInData() throws Exception {
        Customer customer = new Customer("Michał", "Żółtański", "michal@example.pl",
            "+48 123-456-789", LocalDateTime.now());

        Customer.saveExtent(TEST_CUSTOMERS_FILE);
        Customer.clearExtent();
        Customer.loadExtent(TEST_CUSTOMERS_FILE);

        Customer loaded = Customer.getAllCustomers().get(0);
        assertEquals("Michał", loaded.getName());
        assertEquals("Żółtański", loaded.getSurname());
    }
}
