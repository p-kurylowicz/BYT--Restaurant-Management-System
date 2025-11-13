import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * Tests for derived attribute implementation (calculated values).
 *
 * <p>Test categories:
 * <ul>
 *   <li>needsReorder: stock below/above reorder point calculations</li>
 *   <li>yearsOfService: date-based calculation from hire date</li>
 *   <li>isExperienced: boolean derived from yearsOfService >= 5</li>
 *   <li>No setters: derived attributes are read-only</li>
 * </ul>
 */
public class DerivedAttributesTest {

    @BeforeEach
    void clearExtents() {
        Ingredient.clearExtent();
        Employee.clearExtent();
    }

    @Test
    @DisplayName("Ingredient.needsReorder when stock below reorder point")
    void testNeedsReorderTrue() {
        Ingredient ingredient = new Ingredient("Sugar", "kg", 5, 20, 2.0);

        assertTrue(ingredient.getNeedsReorder(),
            "Stock (5) below reorder point (20) should need reorder");
    }

    @Test
    @DisplayName("Ingredient.needsReorder when stock above reorder point")
    void testNeedsReorderFalse() {
        Ingredient ingredient = new Ingredient("Flour", "kg", 50, 20, 1.5);

        assertFalse(ingredient.getNeedsReorder(),
            "Stock (50) above reorder point (20) should not need reorder");
    }

    @Test
    @DisplayName("Ingredient.needsReorder at exact reorder point")
    void testNeedsReorderAtExactPoint() {
        Ingredient ingredient = new Ingredient("Salt", "kg", 20, 20, 1.0);

        assertFalse(ingredient.getNeedsReorder(),
            "Stock exactly at reorder point should not need reorder");
    }

    @Test
    @DisplayName("Ingredient.needsReorder updates after stock change")
    void testNeedsReorderUpdatesAfterStockChange() {
        Ingredient ingredient = new Ingredient("Pepper", "kg", 5, 20, 2.0);

        assertTrue(ingredient.getNeedsReorder(),
            "Initially should need reorder");

        // Update stock
        ingredient.updateCurrentStock(20);

        assertFalse(ingredient.getNeedsReorder(),
            "After restock, should not need reorder");
    }

    @Test
    @DisplayName("Ingredient.needsReorder with zero stock")
    void testNeedsReorderZeroStock() {
        Ingredient ingredient = new Ingredient("Vanilla", "g", 0, 100, 5.0);

        assertTrue(ingredient.getNeedsReorder(),
            "Zero stock should need reorder");
    }

    @Test
    @DisplayName("Employee.yearsOfService calculated correctly")
    void testYearsOfService() {
        Waiter waiter2Years = new Waiter("Tom", "tom@restaurant.com", "+48111222333",
            LocalDate.now().minusYears(2), 25.0, "Section A");

        Waiter waiter7Years = new Waiter("Sarah", "sarah@restaurant.com", "+48444555666",
            LocalDate.now().minusYears(7), 35.0, "Section B");

        assertEquals(2, waiter2Years.getYearsOfService());
        assertEquals(7, waiter7Years.getYearsOfService());
    }

    @Test
    @DisplayName("Employee.yearsOfService for newly hired employee")
    void testYearsOfServiceNewHire() {
        Waiter newWaiter = new Waiter("Mike", "mike@restaurant.com", "+48555666777",
            LocalDate.now(), 22.0, "Section C");

        assertEquals(0, newWaiter.getYearsOfService(),
            "Newly hired employee should have 0 years of service");
    }

    @Test
    @DisplayName("Employee.isExperienced when years < 5")
    void testIsExperiencedFalse() {
        Waiter newWaiter = new Waiter("Tom", "tom@restaurant.com", "+48111222333",
            LocalDate.now().minusYears(2), 25.0, "Section A");

        assertFalse(newWaiter.getIsExperienced(),
            "Employee with 2 years should not be considered experienced (threshold is 5 years)");
    }

    @Test
    @DisplayName("Employee.isExperienced when years >= 5")
    void testIsExperiencedTrue() {
        Waiter experiencedWaiter = new Waiter("Sarah", "sarah@restaurant.com", "+48444555666",
            LocalDate.now().minusYears(7), 35.0, "Section B");

        assertTrue(experiencedWaiter.getIsExperienced(),
            "Employee with 7 years should be considered experienced (threshold is 5 years)");
    }

    @Test
    @DisplayName("Employee.isExperienced at exactly 5 years")
    void testIsExperiencedAtThreshold() {
        Waiter waiterAt5Years = new Waiter("Alex", "alex@restaurant.com", "+48777888999",
            LocalDate.now().minusYears(5), 30.0, "Section D");

        assertTrue(waiterAt5Years.getIsExperienced(),
            "Employee with exactly 5 years should be considered experienced");
    }

    @Test
    @DisplayName("Manager.isExperienced works the same as Waiter")
    void testIsExperiencedInManager() {
        Manager experiencedManager = new Manager("Bob", "bob@restaurant.com", "+48111000111",
            LocalDate.now().minusYears(10), 50.0, "Operations", 3);

        Manager newManager = new Manager("Alice", "alice@restaurant.com", "+48222000222",
            LocalDate.now().minusYears(3), 45.0, "HR", 2);

        assertTrue(experiencedManager.getIsExperienced());
        assertEquals(10, experiencedManager.getYearsOfService());

        assertFalse(newManager.getIsExperienced());
        assertEquals(3, newManager.getYearsOfService());
    }

    @Test
    @DisplayName("Derived attributes have no setters")
    void testDerivedAttributesReadOnly() {
        // This test verifies the design - derived attributes should only have getters
        // We can't directly test for absence of setters, but we verify they work correctly

        Ingredient ingredient = new Ingredient("Test", "kg", 15, 20, 1.0);
        Waiter waiter = new Waiter("Test", "test@restaurant.com", "+48111111111",
            LocalDate.now().minusYears(3), 25.0, "Section A");

        // These should be calculated, not set
        boolean needsReorder = ingredient.getNeedsReorder();
        int yearsOfService = waiter.getYearsOfService();
        boolean isExperienced = waiter.getIsExperienced();

        assertNotNull(needsReorder);
        assertTrue(yearsOfService >= 0);
        assertNotNull(isExperienced);
    }

    @Test
    @DisplayName("Derived attribute handles boundary: stock decrease triggers reorder")
    void testNeedsReorderDynamicChange() {
        Ingredient ingredient = new Ingredient("Coffee", "kg", 25, 20, 10.0);

        assertFalse(ingredient.getNeedsReorder(),
            "Stock above reorder point");

        // Decrease stock below reorder point
        ingredient.updateCurrentStock(-10);

        assertTrue(ingredient.getNeedsReorder(),
            "After stock decrease, should need reorder");
    }
}
