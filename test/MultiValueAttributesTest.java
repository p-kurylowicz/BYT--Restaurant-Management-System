import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests for multi-value attribute implementation (collections).
 *
 * <p>Test categories:
 * <ul>
 *   <li>Multiple elements: handling lists with multiple items</li>
 *   <li>Empty collections: optional multi-value (0..*) support</li>
 *   <li>Encapsulation: unmodifiable view returns</li>
 *   <li>Element validation: individual item constraint checks</li>
 * </ul>
 */
public class MultiValueAttributesTest {

    @BeforeEach
    void clearExtents() {
        MenuItem.clearExtent();
    }

    @Test
    @DisplayName("MenuItem with multiple allergens")
    void testMultipleAllergens() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);
        Set<String> allergens = new HashSet<>(Arrays.asList("Gluten", "Dairy", "Eggs"));

        MainDish pizza = new MainDish("Margherita Pizza", "Classic Italian pizza",
            35.0, "/images/pizza.jpg", "Italian", nutrition, allergens, 2);

        assertEquals(3, pizza.getAllergens().size());
        assertTrue(pizza.getAllergens().contains("Gluten"));
        assertTrue(pizza.getAllergens().contains("Dairy"));
        assertTrue(pizza.getAllergens().contains("Eggs"));
    }

    @Test
    @DisplayName("Multi-value attribute returns unmodifiable collection")
    void testMultiValueAttributeEncapsulation() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, new HashSet<>(Arrays.asList("Gluten")), 2);

        assertThrows(UnsupportedOperationException.class, () -> {
            pizza.getAllergens().add("Dairy");
        }, "Should not be able to modify allergen list directly");
    }

    @Test
    @DisplayName("MenuItem with empty allergen set is valid (optional)")
    void testEmptyAllergenList() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, new HashSet<>(), 2);

        assertEquals(0, pizza.getAllergens().size());
    }

    @Test
    @DisplayName("MenuItem with null allergen set creates empty set")
    void testNullAllergenList() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, 2);

        assertNotNull(pizza.getAllergens());
        assertEquals(0, pizza.getAllergens().size());
    }

    @Test
    @DisplayName("Adding single allergen to MenuItem")
    void testAddSingleAllergen() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, new HashSet<>(Arrays.asList("Gluten")), 2);

        assertEquals(1, pizza.getAllergens().size());

        pizza.addAllergen("Dairy");
        assertEquals(2, pizza.getAllergens().size());
        assertTrue(pizza.getAllergens().contains("Dairy"));
    }

    @Test
    @DisplayName("Cannot add null allergen")
    void testAddNullAllergen() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, new HashSet<>(Arrays.asList("Gluten")), 2);

        assertThrows(IllegalArgumentException.class, () -> {
            pizza.addAllergen(null);
        }, "Cannot add null allergen");
    }

    @Test
    @DisplayName("Cannot add empty string allergen")
    void testAddEmptyAllergen() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, new HashSet<>(Arrays.asList("Gluten")), 2);

        assertThrows(IllegalArgumentException.class, () -> {
            pizza.addAllergen("");
        }, "Cannot add empty string allergen");
    }

    @Test
    @DisplayName("Allergen set cannot contain null values")
    void testAllergenListWithNullElement() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);
        Set<String> allergens = new HashSet<>(Arrays.asList("Gluten", null));

        assertThrows(IllegalArgumentException.class, () -> {
            new MainDish("Pizza", "Description", 35.0, "/img.jpg",
                "Italian", nutrition, allergens, 2);
        }, "Allergen set with null element should throw exception");
    }

    @Test
    @DisplayName("MenuItem with single allergen is valid")
    void testSingleAllergen() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish dish = new MainDish("Tomato Salad", "Simple salad", 15.0, "/img.jpg",
            "Mediterranean", nutrition, new HashSet<>(Arrays.asList("Tomato")), 0);

        assertEquals(1, dish.getAllergens().size());
        assertTrue(dish.getAllergens().contains("Tomato"));
    }

    @Test
    @DisplayName("Setting new allergen set replaces old one")
    void testSetAllergens() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, new HashSet<>(Arrays.asList("Gluten")), 2);

        assertEquals(1, pizza.getAllergens().size());

        pizza.setAllergens(new HashSet<>(Arrays.asList("Dairy", "Eggs")));
        assertEquals(2, pizza.getAllergens().size());
        assertTrue(pizza.getAllergens().contains("Dairy"));
        assertTrue(pizza.getAllergens().contains("Eggs"));
        assertFalse(pizza.getAllergens().contains("Gluten"));
    }

    @Test
    @DisplayName("Removing allergen from MenuItem")
    void testRemoveAllergen() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, new HashSet<>(Arrays.asList("Gluten", "Dairy")), 2);

        assertEquals(2, pizza.getAllergens().size());

        pizza.removeAllergen("Gluten");
        assertEquals(1, pizza.getAllergens().size());
        assertFalse(pizza.getAllergens().contains("Gluten"));
        assertTrue(pizza.getAllergens().contains("Dairy"));
    }

    @Test
    @DisplayName("Clearing all allergens from MenuItem")
    void testClearAllergens() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, new HashSet<>(Arrays.asList("Gluten", "Dairy", "Eggs")), 2);

        assertEquals(3, pizza.getAllergens().size());

        pizza.clearAllergens();
        assertEquals(0, pizza.getAllergens().size());
    }
}
