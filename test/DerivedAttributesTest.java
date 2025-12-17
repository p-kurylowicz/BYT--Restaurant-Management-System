import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class DerivedAttributesTest {

    @BeforeEach
    void clearExtents() {
        Ingredient.clearExtent();
        Employee.clearExtent();
    }

    @Test
    void testNeedsReorderTrue() {
        Ingredient ingredient = new Ingredient("Sugar", "kg", 5, 20, 2.0);

        assertTrue(ingredient.getNeedsReorder());
    }

    @Test
    void testNeedsReorderFalse() {
        Ingredient ingredient = new Ingredient("Flour", "kg", 50, 20, 1.5);

        assertFalse(ingredient.getNeedsReorder());
    }

    @Test
    void testNeedsReorderAtExactPoint() {
        Ingredient ingredient = new Ingredient("Salt", "kg", 20, 20, 1.0);

        assertFalse(ingredient.getNeedsReorder());
    }

    @Test
    void testNeedsReorderUpdatesAfterStockChange() {
        Ingredient ingredient = new Ingredient("Pepper", "kg", 5, 20, 2.0);

        assertTrue(ingredient.getNeedsReorder());

        ingredient.updateCurrentStock(20);

        assertFalse(ingredient.getNeedsReorder());
    }

    @Test
    void testNeedsReorderZeroStock() {
        Ingredient ingredient = new Ingredient("Vanilla", "g", 0, 100, 5.0);

        assertTrue(ingredient.getNeedsReorder());
    }

    @Test
    void testYearsOfService() {
        Employee waiter2Years = new Employee("Tom", "+48111222333", "tom@restaurant.com", "123 Test St",
            LocalDate.now().minusYears(2), 25.0, "Section A");

        Employee waiter7Years = new Employee("Sarah", "+48444555666", "sarah@restaurant.com", "456 Test Ave",
            LocalDate.now().minusYears(7), 35.0, "Section B");

        assertEquals(2, waiter2Years.getYearsOfService());
        assertEquals(7, waiter7Years.getYearsOfService());
    }

    @Test
    void testYearsOfServiceNewHire() {
        Employee newWaiter = new Employee("Mike", "+48555666777", "mike@restaurant.com", "789 Test Rd",
            LocalDate.now(), 22.0, "Section C");

        assertEquals(0, newWaiter.getYearsOfService());
    }

    @Test
    void testIsExperiencedFalse() {
        Employee newWaiter = new Employee("Tom", "+48111222333", "tom@restaurant.com", "123 Test St",
            LocalDate.now().minusYears(2), 25.0, "Section A");

        assertFalse(newWaiter.getIsExperienced());
    }

    @Test
    void testIsExperiencedTrue() {
        Employee experiencedWaiter = new Employee("Sarah", "+48444555666", "sarah@restaurant.com", "456 Test Ave",
            LocalDate.now().minusYears(7), 35.0, "Section B");

        assertTrue(experiencedWaiter.getIsExperienced());
    }

    @Test
    void testIsExperiencedAtThreshold() {
        Employee waiterAt5Years = new Employee("Alex", "+48777888999", "alex@restaurant.com", "321 Test Blvd",
            LocalDate.now().minusYears(5), 30.0, "Section D");

        assertTrue(waiterAt5Years.getIsExperienced());
    }

    @Test
    void testIsExperiencedInManager() {
        Employee experiencedManager = new Employee("Bob", "+48111000111", "bob@restaurant.com", "100 Manager St",
            LocalDate.now().minusYears(10), 50.0, "Operations", 3);

        Employee newManager = new Employee("Alice", "+48222000222", "alice@restaurant.com", "200 Manager Ave",
            LocalDate.now().minusYears(3), 45.0, "HR", 2);

        assertTrue(experiencedManager.getIsExperienced());
        assertEquals(10, experiencedManager.getYearsOfService());

        assertFalse(newManager.getIsExperienced());
        assertEquals(3, newManager.getYearsOfService());
    }

    @Test
    void testDerivedAttributesReadOnly() {
        Ingredient ingredient = new Ingredient("Test", "kg", 15, 20, 1.0);
        Employee waiter = new Employee("Test", "+48111111111", "test@restaurant.com", "999 Test Ln",
            LocalDate.now().minusYears(3), 25.0, "Section A");

        boolean needsReorder = ingredient.getNeedsReorder();
        int yearsOfService = waiter.getYearsOfService();
        boolean isExperienced = waiter.getIsExperienced();

        assertNotNull(needsReorder);
        assertTrue(yearsOfService >= 0);
        assertNotNull(isExperienced);
    }

    @Test
    void testNeedsReorderDynamicChange() {
        Ingredient ingredient = new Ingredient("Coffee", "kg", 25, 20, 10.0);

        assertFalse(ingredient.getNeedsReorder());

        ingredient.updateCurrentStock(-10);

        assertTrue(ingredient.getNeedsReorder());
    }
}
