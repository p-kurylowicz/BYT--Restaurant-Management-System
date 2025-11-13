import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

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
        Ingredient.clearExtent();
        MenuItem.clearExtent();
    }

    @Test
    @DisplayName("MenuItem with multiple ingredients")
    void testMultipleIngredients() {
        Ingredient ing1 = new Ingredient("Tomato", "kg", 50, 10, 2.5);
        Ingredient ing2 = new Ingredient("Cheese", "kg", 30, 5, 8.0);
        Ingredient ing3 = new Ingredient("Basil", "bunch", 20, 3, 1.5);

        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);
        List<Ingredient> ingredients = Arrays.asList(ing1, ing2, ing3);

        MainDish pizza = new MainDish("Margherita Pizza", "Classic Italian pizza",
            35.0, "/images/pizza.jpg", "Italian", nutrition, ingredients, 2);

        assertEquals(3, pizza.getIngredients().size());
        assertTrue(pizza.getIngredients().contains(ing1));
        assertTrue(pizza.getIngredients().contains(ing2));
        assertTrue(pizza.getIngredients().contains(ing3));
    }

    @Test
    @DisplayName("Multi-value attribute returns unmodifiable collection")
    void testMultiValueAttributeEncapsulation() {
        Ingredient ing1 = new Ingredient("Tomato", "kg", 50, 10, 2.5);
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, Arrays.asList(ing1), 2);

        assertThrows(UnsupportedOperationException.class, () -> {
            pizza.getIngredients().add(new Ingredient("Oregano", "g", 100, 10, 0.5));
        }, "Should not be able to modify ingredient list directly");
    }

    @Test
    @DisplayName("MenuItem requires at least one ingredient")
    void testMinimumIngredientConstraint() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        assertThrows(IllegalArgumentException.class, () -> {
            new MainDish("Empty Pizza", "No ingredients", 35.0, "/img.jpg",
                "Italian", nutrition, new ArrayList<>(), 2);
        }, "Should require at least one ingredient");
    }

    @Test
    @DisplayName("Null ingredient list should throw exception")
    void testNullIngredientList() {
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        assertThrows(IllegalArgumentException.class, () -> {
            new MainDish("Pizza", "Description", 35.0, "/img.jpg",
                "Italian", nutrition, null, 2);
        }, "Null ingredient list should throw exception");
    }

    @Test
    @DisplayName("Adding single ingredient to MenuItem")
    void testAddSingleIngredient() {
        Ingredient ing1 = new Ingredient("Tomato", "kg", 50, 10, 2.5);
        Ingredient ing2 = new Ingredient("Cheese", "kg", 30, 5, 8.0);
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, Arrays.asList(ing1), 2);

        assertEquals(1, pizza.getIngredients().size());

        pizza.addIngredient(ing2);
        assertEquals(2, pizza.getIngredients().size());
        assertTrue(pizza.getIngredients().contains(ing2));
    }

    @Test
    @DisplayName("Cannot add null ingredient")
    void testAddNullIngredient() {
        Ingredient ing1 = new Ingredient("Tomato", "kg", 50, 10, 2.5);
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, Arrays.asList(ing1), 2);

        assertThrows(IllegalArgumentException.class, () -> {
            pizza.addIngredient(null);
        }, "Cannot add null ingredient");
    }

    @Test
    @DisplayName("Ingredient list cannot contain null values")
    void testIngredientListWithNullElement() {
        Ingredient ing1 = new Ingredient("Tomato", "kg", 50, 10, 2.5);
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);
        List<Ingredient> ingredients = Arrays.asList(ing1, null);

        assertThrows(IllegalArgumentException.class, () -> {
            new MainDish("Pizza", "Description", 35.0, "/img.jpg",
                "Italian", nutrition, ingredients, 2);
        }, "Ingredient list with null element should throw exception");
    }

    @Test
    @DisplayName("MenuItem with single ingredient is valid")
    void testSingleIngredient() {
        Ingredient ing = new Ingredient("Tomato", "kg", 50, 10, 2.5);
        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish dish = new MainDish("Tomato Salad", "Simple salad", 15.0, "/img.jpg",
            "Mediterranean", nutrition, Arrays.asList(ing), 0);

        assertEquals(1, dish.getIngredients().size());
        assertEquals(ing, dish.getIngredients().get(0));
    }

    @Test
    @DisplayName("Setting new ingredient list replaces old one")
    void testSetIngredients() {
        Ingredient ing1 = new Ingredient("Tomato", "kg", 50, 10, 2.5);
        Ingredient ing2 = new Ingredient("Cheese", "kg", 30, 5, 8.0);
        Ingredient ing3 = new Ingredient("Basil", "bunch", 20, 3, 1.5);

        NutritionalInfo nutrition = new NutritionalInfo(350, 15.0, 40.0, 12.0, 3.0);

        MainDish pizza = new MainDish("Pizza", "Description", 35.0, "/img.jpg",
            "Italian", nutrition, Arrays.asList(ing1), 2);

        assertEquals(1, pizza.getIngredients().size());

        pizza.setIngredients(Arrays.asList(ing2, ing3));
        assertEquals(2, pizza.getIngredients().size());
        assertTrue(pizza.getIngredients().contains(ing2));
        assertTrue(pizza.getIngredients().contains(ing3));
        assertFalse(pizza.getIngredients().contains(ing1));
    }
}
