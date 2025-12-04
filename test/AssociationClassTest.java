import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class AssociationClassTest {

    @BeforeEach
    void setup() {
        ItemQuantity.clearExtent();
        OrderRequest.clearExtent();
        MenuItem.clearExtent();
        SupplyLog.clearExtent();
        Supplier.clearExtent();
        Ingredient.clearExtent();
    }

    // ============================================
    // ITEMQUANTITY TESTS (Regular Association Class)
    // ============================================

    @Test
    @DisplayName("ItemQuantity: Create establishes reverse connections")
    void testItemQuantityCreation() {
        OrderRequest request = new OrderRequest();
        NutritionalInfo nutrition = new NutritionalInfo(100, 10, 10, 10, 5);
        MenuItem item = new MainDish("Pasta", "Italian pasta", 25.0, "img", "Italy", nutrition, 1);

        ItemQuantity iq = ItemQuantity.create(request, item, 3, "Extra cheese");

        // Check reverse connections
        assertTrue(request.getItemQuantities().contains(iq));
        assertTrue(item.getItemQuantities().contains(iq));
        assertEquals(3, iq.getQuantity());
        assertEquals("Extra cheese", iq.getSpecialRequests());
    }

    @Test
    @DisplayName("ItemQuantity: Create without special requests")
    void testItemQuantityCreationNoSpecialRequests() {
        OrderRequest request = new OrderRequest();
        NutritionalInfo nutrition = new NutritionalInfo(100, 10, 10, 10, 5);
        MenuItem item = new MainDish("Pizza", "Italian pizza", 30.0, "img", "Italy", nutrition, 2);

        ItemQuantity iq = ItemQuantity.create(request, item, 2);

        assertNotNull(iq);
        assertEquals(2, iq.getQuantity());
        assertNull(iq.getSpecialRequests());
    }

    @Test
    @DisplayName("ItemQuantity: Delete removes all 4 references")
    void testItemQuantityDelete() {
        OrderRequest request = new OrderRequest();
        NutritionalInfo nutrition = new NutritionalInfo(100, 10, 10, 10, 5);
        MenuItem item = new MainDish("Salad", "Fresh salad", 15.0, "img", "Italy", nutrition, 0);

        ItemQuantity iq = ItemQuantity.create(request, item, 1);

        // Verify connections exist
        assertTrue(request.getItemQuantities().contains(iq));
        assertTrue(item.getItemQuantities().contains(iq));
        assertTrue(ItemQuantity.getAllItemQuantities().contains(iq));

        iq.delete();

        // All 4 references removed
        assertFalse(request.getItemQuantities().contains(iq));
        assertFalse(item.getItemQuantities().contains(iq));
        assertFalse(ItemQuantity.getAllItemQuantities().contains(iq));
        assertNull(iq.getOrderRequest());
        assertNull(iq.getMenuItem());
    }

    @Test
    @DisplayName("ItemQuantity: Null OrderRequest throws exception")
    void testItemQuantityNullOrderRequest() {
        NutritionalInfo nutrition = new NutritionalInfo(100, 10, 10, 10, 5);
        MenuItem item = new MainDish("Soup", "Hot soup", 20.0, "img", "Poland", nutrition, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            ItemQuantity.create(null, item, 2);
        });
    }

    @Test
    @DisplayName("ItemQuantity: Null MenuItem throws exception")
    void testItemQuantityNullMenuItem() {
        OrderRequest request = new OrderRequest();

        assertThrows(IllegalArgumentException.class, () -> {
            ItemQuantity.create(request, null, 2);
        });
    }

    @Test
    @DisplayName("ItemQuantity: Invalid quantity throws exception")
    void testItemQuantityInvalidQuantity() {
        OrderRequest request = new OrderRequest();
        NutritionalInfo nutrition = new NutritionalInfo(100, 10, 10, 10, 5);
        MenuItem item = new MainDish("Burger", "Beef burger", 35.0, "img", "USA", nutrition, 2);

        assertThrows(IllegalArgumentException.class, () -> {
            ItemQuantity.create(request, item, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ItemQuantity.create(request, item, -5);
        });
    }

    @Test
    @DisplayName("ItemQuantity: Empty special requests throws exception")
    void testItemQuantityEmptySpecialRequests() {
        OrderRequest request = new OrderRequest();
        NutritionalInfo nutrition = new NutritionalInfo(100, 10, 10, 10, 5);
        MenuItem item = new MainDish("Steak", "Grilled steak", 50.0, "img", "USA", nutrition, 3);

        ItemQuantity iq = ItemQuantity.create(request, item, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            iq.setSpecialRequests("   ");
        });
    }

    @Test
    @DisplayName("ItemQuantity: Calculate request total")
    void testItemQuantityCalculateTotal() {
        OrderRequest request = new OrderRequest();
        NutritionalInfo nutrition = new NutritionalInfo(100, 10, 10, 10, 5);
        MenuItem item = new MainDish("Pasta", "Italian pasta", 25.0, "img", "Italy", nutrition, 1);

        ItemQuantity iq = ItemQuantity.create(request, item, 3);

        // 25.0 * 1.23 (with tax) * 3 = 92.25
        assertEquals(92.25, iq.getRequestTotal(), 0.01);
    }

    // ============================================
    // SUPPLYLOG TESTS ({Bag} Association Class)
    // ============================================

    @Test
    @DisplayName("SupplyLog: Create establishes reverse connections")
    void testSupplyLogCreation() {
        Supplier supplier = new Supplier("Farm Fresh", "123", "farm@test.com", "Farm Road", 4.5, "John");
        Ingredient ingredient = new Ingredient("Tomato", "kg", 100, 50, 5.0);

        SupplyLog log = SupplyLog.create(supplier, ingredient, LocalDate.now(), 4.8, 20.0);

        // Check reverse connections
        assertTrue(supplier.getSupplyLogs().contains(log));
        assertTrue(ingredient.getSupplyLogs().contains(log));
        assertEquals(supplier, log.getSupplier());
        assertEquals(ingredient, log.getIngredient());
        assertEquals(4.8, log.getCostAtSupply());
        assertEquals(20.0, log.getQuantitySupplied());
    }

    @Test
    @DisplayName("SupplyLog: {Bag} allows multiple logs for same pair")
    void testSupplyLogBagBehavior() {
        Supplier supplier = new Supplier("Fresh Foods", "456", "fresh@test.com", "Food St", 4.0, "Jane");
        Ingredient ingredient = new Ingredient("Carrot", "kg", 50, 20, 3.0);

        // Create multiple logs for same supplier-ingredient pair
        SupplyLog log1 = SupplyLog.create(supplier, ingredient, LocalDate.of(2024, 1, 15), 3.0, 10.0);
        SupplyLog log2 = SupplyLog.create(supplier, ingredient, LocalDate.of(2024, 2, 20), 3.2, 15.0);
        SupplyLog log3 = SupplyLog.create(supplier, ingredient, LocalDate.of(2024, 3, 10), 2.9, 12.0);

        // All three should exist (no duplicate checking)
        assertEquals(3, supplier.getSupplyLogs().size());
        assertEquals(3, ingredient.getSupplyLogs().size());
        assertTrue(supplier.getSupplyLogs().contains(log1));
        assertTrue(supplier.getSupplyLogs().contains(log2));
        assertTrue(supplier.getSupplyLogs().contains(log3));
    }

    @Test
    @DisplayName("SupplyLog: Delete removes all 4 references")
    void testSupplyLogDelete() {
        Supplier supplier = new Supplier("Organic Co", "789", "organic@test.com", "Org Ave", 5.0, "Bob");
        Ingredient ingredient = new Ingredient("Lettuce", "kg", 30, 15, 4.0);

        SupplyLog log = SupplyLog.create(supplier, ingredient, LocalDate.now(), 4.2, 8.0);

        // Verify connections exist
        assertTrue(supplier.getSupplyLogs().contains(log));
        assertTrue(ingredient.getSupplyLogs().contains(log));
        assertTrue(SupplyLog.getAllSupplyLogs().contains(log));

        log.delete();

        // All 4 references removed
        assertFalse(supplier.getSupplyLogs().contains(log));
        assertFalse(ingredient.getSupplyLogs().contains(log));
        assertFalse(SupplyLog.getAllSupplyLogs().contains(log));
        assertNull(log.getSupplier());
        assertNull(log.getIngredient());
    }

    @Test
    @DisplayName("SupplyLog: Null Supplier throws exception")
    void testSupplyLogNullSupplier() {
        Ingredient ingredient = new Ingredient("Onion", "kg", 40, 20, 2.5);

        assertThrows(IllegalArgumentException.class, () -> {
            SupplyLog.create(null, ingredient, LocalDate.now(), 2.5, 10.0);
        });
    }

    @Test
    @DisplayName("SupplyLog: Null Ingredient throws exception")
    void testSupplyLogNullIngredient() {
        Supplier supplier = new Supplier("Veggie Ltd", "999", "veggie@test.com", "Veg St", 3.5, "Alice");

        assertThrows(IllegalArgumentException.class, () -> {
            SupplyLog.create(supplier, null, LocalDate.now(), 2.0, 5.0);
        });
    }

    @Test
    @DisplayName("SupplyLog: Future date throws exception")
    void testSupplyLogFutureDate() {
        Supplier supplier = new Supplier("Future Foods", "111", "future@test.com", "Time St", 4.0, "Doc");
        Ingredient ingredient = new Ingredient("Potato", "kg", 80, 40, 1.5);

        assertThrows(IllegalArgumentException.class, () -> {
            SupplyLog.create(supplier, ingredient, LocalDate.now().plusDays(1), 1.5, 10.0);
        });
    }

    @Test
    @DisplayName("SupplyLog: Negative cost throws exception")
    void testSupplyLogNegativeCost() {
        Supplier supplier = new Supplier("Cheap Co", "222", "cheap@test.com", "Low St", 3.0, "Penny");
        Ingredient ingredient = new Ingredient("Pepper", "kg", 20, 10, 8.0);

        assertThrows(IllegalArgumentException.class, () -> {
            SupplyLog.create(supplier, ingredient, LocalDate.now(), -5.0, 10.0);
        });
    }

    @Test
    @DisplayName("SupplyLog: Zero/negative quantity throws exception")
    void testSupplyLogInvalidQuantity() {
        Supplier supplier = new Supplier("Quality Foods", "333", "quality@test.com", "Q St", 4.5, "Quinn");
        Ingredient ingredient = new Ingredient("Garlic", "kg", 15, 5, 12.0);

        assertThrows(IllegalArgumentException.class, () -> {
            SupplyLog.create(supplier, ingredient, LocalDate.now(), 12.0, 0.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            SupplyLog.create(supplier, ingredient, LocalDate.now(), 12.0, -10.0);
        });
    }

    @Test
    @DisplayName("SupplyLog: Multiple deletes from same pair")
    void testSupplyLogMultipleDeletes() {
        Supplier supplier = new Supplier("Multi Supply", "444", "multi@test.com", "M St", 4.2, "Mike");
        Ingredient ingredient = new Ingredient("Cucumber", "kg", 25, 10, 3.5);

        SupplyLog log1 = SupplyLog.create(supplier, ingredient, LocalDate.of(2024, 1, 1), 3.5, 5.0);
        SupplyLog log2 = SupplyLog.create(supplier, ingredient, LocalDate.of(2024, 2, 1), 3.6, 8.0);
        SupplyLog log3 = SupplyLog.create(supplier, ingredient, LocalDate.of(2024, 3, 1), 3.4, 6.0);

        assertEquals(3, supplier.getSupplyLogs().size());

        log1.delete();
        assertEquals(2, supplier.getSupplyLogs().size());
        assertTrue(supplier.getSupplyLogs().contains(log2));
        assertTrue(supplier.getSupplyLogs().contains(log3));

        log2.delete();
        assertEquals(1, supplier.getSupplyLogs().size());
        assertTrue(supplier.getSupplyLogs().contains(log3));

        log3.delete();
        assertEquals(0, supplier.getSupplyLogs().size());
    }
}
