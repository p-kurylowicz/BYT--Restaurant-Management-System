import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

/**
 * Tests for complex attribute implementation (NutritionalInfo).
 *
 * <p>Test categories:
 * <ul>
 *   <li>Creation: valid grouped attribute construction</li>
 *   <li>Validation: negative value checks for all components</li>
 *   <li>Atomic updates: all components updated together</li>
 *   <li>Integration: complex attribute used in MenuItem</li>
 * </ul>
 */
public class ComplexAttributesTest {

    @BeforeEach
    void clearExtents() {
        Ingredient.clearExtent();
        MenuItem.clearExtent();
    }

    @Test
    @DisplayName("Create NutritionalInfo with valid values")
    void testNutritionalInfoCreation() {
        NutritionalInfo nutrition = new NutritionalInfo(450, 25.5, 35.0, 18.0, 5.5);

        assertEquals(450, nutrition.getCalories());
        assertEquals(25.5, nutrition.getProtein());
        assertEquals(35.0, nutrition.getCarbs());
        assertEquals(18.0, nutrition.getFats());
        assertEquals(5.5, nutrition.getFiber());
    }

    @Test
    @DisplayName("Negative calories should throw IllegalArgumentException")
    void testNegativeCaloriesValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NutritionalInfo(-100, 25.5, 35.0, 18.0, 5.5);
        }, "Negative calories in complex attribute should throw exception");
    }

    @Test
    @DisplayName("Negative protein should throw IllegalArgumentException")
    void testNegativeProteinValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NutritionalInfo(450, -25.5, 35.0, 18.0, 5.5);
        }, "Negative protein should throw exception");
    }

    @Test
    @DisplayName("Negative carbs should throw IllegalArgumentException")
    void testNegativeCarbsValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NutritionalInfo(450, 25.5, -35.0, 18.0, 5.5);
        }, "Negative carbs should throw exception");
    }

    @Test
    @DisplayName("Negative fats should throw IllegalArgumentException")
    void testNegativeFatsValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NutritionalInfo(450, 25.5, 35.0, -18.0, 5.5);
        }, "Negative fats should throw exception");
    }

    @Test
    @DisplayName("Negative fiber should throw IllegalArgumentException")
    void testNegativeFiberValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NutritionalInfo(450, 25.5, 35.0, 18.0, -5.5);
        }, "Negative fiber should throw exception");
    }

    @Test
    @DisplayName("Complex attribute updated as atomic unit")
    void testComplexAttributeAtomicUpdate() {
        NutritionalInfo nutrition1 = new NutritionalInfo(100, 10, 20, 5, 2);
        NutritionalInfo nutrition2 = new NutritionalInfo(200, 20, 40, 10, 4);

        MainDish dish = new MainDish("Test Dish", "Description", 10.0, "/img.jpg",
            "Italian", nutrition1,  1);

        assertEquals(100, dish.getNutritionalInfo().getCalories());

        // Complex attribute updated as atomic unit
        dish.setNutritionalInfo(nutrition2);
        assertEquals(200, dish.getNutritionalInfo().getCalories());
        assertEquals(20, dish.getNutritionalInfo().getProtein());
        assertEquals(40, dish.getNutritionalInfo().getCarbs());
        assertEquals(10, dish.getNutritionalInfo().getFats());
        assertEquals(4, dish.getNutritionalInfo().getFiber());
    }

    @Test
    @DisplayName("MenuItem cannot be created without NutritionalInfo")
    void testNutritionalInfoRequired() {

        assertThrows(IllegalArgumentException.class, () -> {
            new MainDish("Test Dish", "Description", 10.0, "/img.jpg",
                "Italian", null,  1);
        }, "MenuItem should require NutritionalInfo");
    }

    @Test
    @DisplayName("NutritionalInfo with zero values should be valid")
    void testZeroValuesInNutritionalInfo() {
        NutritionalInfo nutrition = new NutritionalInfo(0, 0, 0, 0, 0);

        assertEquals(0, nutrition.getCalories());
        assertEquals(0, nutrition.getProtein());
        assertEquals(0, nutrition.getCarbs());
        assertEquals(0, nutrition.getFats());
        assertEquals(0, nutrition.getFiber());
    }

    @Test
    @DisplayName("NutritionalInfo in all MenuItem subclasses")
    void testNutritionalInfoInSubclasses() {
        NutritionalInfo nutrition = new NutritionalInfo(300, 15, 30, 10, 3);

        // MainDish
        MainDish mainDish = new MainDish("Pasta", "Description", 20.0, "/img.jpg",
            "Italian", nutrition,  2);
        assertNotNull(mainDish.getNutritionalInfo());

        // Beverage
        Beverage beverage = new Beverage("Juice", "Description", 5.0, "/img.jpg",
            "USA", nutrition,  0.0);
        assertNotNull(beverage.getNutritionalInfo());

        // Dessert
        Dessert dessert = new Dessert("Cake", "Description", 15.0, "/img.jpg",
            "French", nutrition,  true);
        assertNotNull(dessert.getNutritionalInfo());
    }
}
