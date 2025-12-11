import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Tests for class extent management (object tracking).
 *
 * <p>Test categories:
 * <ul>
 *   <li>Auto-registration: objects added to extent on creation</li>
 *   <li>Retrieval: getAllX() returns all created instances</li>
 *   <li>Encapsulation: returned collections are unmodifiable</li>
 *   <li>Clearing: clearExtent() removes all instances</li>
 * </ul>
 */
public class ClassExtentTest {

    @BeforeEach
    void clearExtents() {
        Customer.clearExtent();
        Ingredient.clearExtent();
        Table.clearExtent();
        MenuItem.clearExtent();
    }

    @Test
    @DisplayName("Objects automatically added to extent on creation")
    void testObjectsAutoAddedToExtent() {
        assertEquals(0, Customer.getAllCustomersFromExtent().size());

        Customer c1 = new Customer("Bob", "Johnson", "bob@example.com",
            "+48111111111", LocalDateTime.now());
        assertEquals(1, Customer.getAllCustomersFromExtent().size());

        Customer c2 = new Customer("Mary", "Williams", "mary@example.com",
            "+48222222222", LocalDateTime.now());
        assertEquals(2, Customer.getAllCustomersFromExtent().size());

        assertTrue(Customer.getAllCustomersFromExtent().contains(c1));
        assertTrue(Customer.getAllCustomersFromExtent().contains(c2));
    }

    @Test
    @DisplayName("Extent returns unmodifiable collection")
    void testExtentEncapsulation() {
        Customer c1 = new Customer("Bob", "Johnson", "bob@example.com",
            "+48111111111", LocalDateTime.now());

        assertThrows(UnsupportedOperationException.class, () -> {
            Customer.getAllCustomersFromExtent().add(c1);
        }, "Should not be able to modify extent directly");
    }

    @Test
    @DisplayName("Extent cannot be modified by removing elements")
    void testExtentCannotRemove() {
        Customer c1 = new Customer("Bob", "Johnson", "bob@example.com",
            "+48111111111", LocalDateTime.now());

        assertThrows(UnsupportedOperationException.class, () -> {
            Customer.getAllCustomersFromExtent().remove(c1);
        }, "Should not be able to remove from extent directly");
    }

    @Test
    @DisplayName("Extent cannot be cleared directly")
    void testExtentCannotClearDirectly() {
        Customer c1 = new Customer("Bob", "Johnson", "bob@example.com",
            "+48111111111", LocalDateTime.now());

        assertThrows(UnsupportedOperationException.class, () -> {
            Customer.getAllCustomersFromExtent().clear();
        }, "Should not be able to clear extent directly");
    }

    @Test
    @DisplayName("Multiple classes maintain separate extents")
    void testMultipleClassExtents() {
        new Customer("Test", "User", "test@example.com", "+48111111111", LocalDateTime.now());
        new Ingredient("Salt", "kg", 100, 10, 0.5);
        new Ingredient("Pepper", "kg", 80, 10, 2.0);
        new Table(1, 4, "Main Hall");

        assertEquals(1, Customer.getAllCustomersFromExtent().size());
        assertEquals(2, Ingredient.getAllIngredients().size());
        assertEquals(1, Table.getAllTablesFromExtent().size());
    }

    @Test
    @DisplayName("Extent clearExtent() method works")
    void testClearExtent() {
        new Customer("Test1", "User1", "test1@example.com", "+48111111111", LocalDateTime.now());
        new Customer("Test2", "User2", "test2@example.com", "+48222222222", LocalDateTime.now());
        new Customer("Test3", "User3", "test3@example.com", "+48333333333", LocalDateTime.now());

        assertEquals(3, Customer.getAllCustomersFromExtent().size());

        Customer.clearExtent();
        assertEquals(0, Customer.getAllCustomersFromExtent().size());
    }

    @Test
    @DisplayName("Extent tracks objects in order of creation")
    void testExtentOrder() {
        Customer c1 = new Customer("First", "Customer", "first@example.com",
            "+48111111111", LocalDateTime.now());
        Customer c2 = new Customer("Second", "Customer", "second@example.com",
            "+48222222222", LocalDateTime.now());
        Customer c3 = new Customer("Third", "Customer", "third@example.com",
            "+48333333333", LocalDateTime.now());

        assertEquals(3, Customer.getAllCustomersFromExtent().size());
        assertEquals(c1, Customer.getAllCustomersFromExtent().get(0));
        assertEquals(c2, Customer.getAllCustomersFromExtent().get(1));
        assertEquals(c3, Customer.getAllCustomersFromExtent().get(2));
    }

    @Test
    @DisplayName("Extent persists references, not copies")
    void testExtentReferences() {
        Customer customer = new Customer("John", "Doe", "john@example.com",
            "+48111111111", LocalDateTime.now());

        customer.setPhone("+48999999999");

        Customer fromExtent = Customer.getAllCustomersFromExtent().get(0);
        assertEquals("+48999999999", fromExtent.getPhone(),
            "Extent should contain reference to actual object");
    }

    @Test
    @DisplayName("Extent works with inheritance (abstract MenuItem)")
    void testExtentWithInheritance() {
        NutritionalInfo nutrition = new NutritionalInfo(300, 15, 30, 10, 3);

        MainDish mainDish = new MainDish("Pasta", "Italian pasta", 25.0, "/img.jpg",
            "Italian", nutrition, 1);
        Beverage beverage = new Beverage("Water", "Mineral water", 3.0, "/img.jpg",
            "Poland", nutrition, 0.0);

        // Both should be in MenuItem extent
        assertEquals(2, MenuItem.getAllMenuItems().size());
        assertTrue(MenuItem.getAllMenuItems().contains(mainDish));
        assertTrue(MenuItem.getAllMenuItems().contains(beverage));
    }

    @Test
    @DisplayName("Empty extent returns empty list, not null")
    void testEmptyExtentNotNull() {
        Customer.clearExtent();

        assertNotNull(Customer.getAllCustomersFromExtent());
        assertEquals(0, Customer.getAllCustomersFromExtent().size());
        assertTrue(Customer.getAllCustomersFromExtent().isEmpty());
    }

    @Test
    @DisplayName("Large extent handles many objects")
    void testLargeExtent() {
        for (int i = 0; i < 100; i++) {
            new Customer("Customer" + i, "User" + i, "user" + i + "@example.com",
                "+48" + String.format("%09d", i), LocalDateTime.now());
        }

        assertEquals(100, Customer.getAllCustomersFromExtent().size());
    }

    @Test
    @DisplayName("Extent survives invalid object creation attempts")
    void testExtentAfterFailedCreation() {
        Customer c1 = new Customer("Valid", "Customer", "valid@example.com",
            "+48111111111", LocalDateTime.now());

        assertEquals(1, Customer.getAllCustomersFromExtent().size());

        // Try to create invalid customer (should throw exception)
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer("", "Invalid", "invalid@example.com",
                "+48222222222", LocalDateTime.now());
        });

        // Extent should still contain only valid customer
        assertEquals(1, Customer.getAllCustomersFromExtent().size());
        assertEquals(c1, Customer.getAllCustomersFromExtent().get(0));
    }
}
