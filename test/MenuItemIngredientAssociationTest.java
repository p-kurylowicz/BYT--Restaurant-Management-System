import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MenuItemIngredientAssociationTest {

    @BeforeEach
    void setup() {
        MenuItem.clearExtent();
        Ingredient.clearExtent();
    }

    @Test
    @DisplayName("1..*: Adding ingredient to MenuItem establishes reverse connection")
    void testAddIngredientEstablishesReverseConnection() {
        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 50, 15, 5);
        MenuItem menuItem = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo, 1);
        Ingredient ingredient = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);

        menuItem.addIngredient(ingredient);

        assertTrue(menuItem.getIngredients().contains(ingredient));
        assertTrue(ingredient.getMenuItems().contains(menuItem));
    }

    @Test
    @DisplayName("1..*: Adding MenuItem to Ingredient establishes reverse connection")
    void testAddMenuItemEstablishesReverseConnection() {
        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 50, 15, 5);
        MenuItem menuItem = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo, 1);
        Ingredient ingredient = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);

        ingredient.addMenuItem(menuItem);

        assertTrue(ingredient.getMenuItems().contains(menuItem));
        assertTrue(menuItem.getIngredients().contains(ingredient));
    }

    @Test
    @DisplayName("1..*: Cannot remove last ingredient from MenuItem")
    void testCannotRemoveLastIngredient() {
        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 50, 15, 5);
        MenuItem menuItem = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo, 1);
        Ingredient ingredient = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);

        menuItem.addIngredient(ingredient);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            menuItem.removeIngredient(ingredient);
        });
        assertTrue(exception.getMessage().contains("Cannot remove the last ingredient"));
        assertTrue(exception.getMessage().contains("1..*"));
    }

    @Test
    @DisplayName("1..*: Cannot remove last MenuItem from Ingredient")
    void testCannotRemoveLastMenuItem() {
        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 50, 15, 5);
        MenuItem menuItem = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo, 1);
        Ingredient ingredient = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);

        ingredient.addMenuItem(menuItem);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ingredient.removeMenuItem(menuItem);
        });
        assertTrue(exception.getMessage().contains("Cannot remove the last menu item"));
        assertTrue(exception.getMessage().contains("1..*"));
    }

    @Test
    @DisplayName("1..*: Can remove ingredient when multiple exist")
    void testCanRemoveIngredientWhenMultipleExist() {
        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 50, 15, 5);
        MenuItem menuItem = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo, 1);
        Ingredient ingredient1 = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);
        Ingredient ingredient2 = new Ingredient("Basil", "kg", 5.0, 1.0, 10.0);

        menuItem.addIngredient(ingredient1);
        menuItem.addIngredient(ingredient2);

        assertEquals(2, menuItem.getIngredients().size());

        assertDoesNotThrow(() -> {
            menuItem.removeIngredient(ingredient1);
        });

        assertFalse(menuItem.getIngredients().contains(ingredient1));
        assertTrue(menuItem.getIngredients().contains(ingredient2));
        assertFalse(ingredient1.getMenuItems().contains(menuItem));
    }

    @Test
    @DisplayName("1..*: Can remove MenuItem when multiple exist")
    void testCanRemoveMenuItemWhenMultipleExist() {
        NutritionalInfo nutritionalInfo1 = new NutritionalInfo(500, 20, 50, 15, 5);
        NutritionalInfo nutritionalInfo2 = new NutritionalInfo(300, 10, 30, 10, 3);
        MenuItem menuItem1 = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo1, 1);
        MenuItem menuItem2 = new MainDish("Pizza", "Tasty pizza", 30.0, "pizza.jpg", "Italy", nutritionalInfo2, 2);
        Ingredient ingredient = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);

        ingredient.addMenuItem(menuItem1);
        ingredient.addMenuItem(menuItem2);

        assertEquals(2, ingredient.getMenuItems().size());

        assertDoesNotThrow(() -> {
            ingredient.removeMenuItem(menuItem1);
        });

        assertFalse(ingredient.getMenuItems().contains(menuItem1));
        assertTrue(ingredient.getMenuItems().contains(menuItem2));
        assertFalse(menuItem1.getIngredients().contains(ingredient));
    }

    @Test
    @DisplayName("1..*: Null validation for addIngredient")
    void testAddIngredientNullValidation() {
        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 50, 15, 5);
        MenuItem menuItem = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo, 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuItem.addIngredient(null);
        });
        assertTrue(exception.getMessage().contains("Ingredient cannot be null"));
    }

    @Test
    @DisplayName("1..*: Null validation for addMenuItem")
    void testAddMenuItemNullValidation() {
        Ingredient ingredient = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ingredient.addMenuItem(null);
        });
        assertTrue(exception.getMessage().contains("MenuItem cannot be null"));
    }

    @Test
    @DisplayName("1..*: Null validation for removeIngredient")
    void testRemoveIngredientNullValidation() {
        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 50, 15, 5);
        MenuItem menuItem = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo, 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuItem.removeIngredient(null);
        });
        assertTrue(exception.getMessage().contains("Ingredient cannot be null"));
    }

    @Test
    @DisplayName("1..*: Null validation for removeMenuItem")
    void testRemoveMenuItemNullValidation() {
        Ingredient ingredient = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);
        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 50, 15, 5);
        MenuItem menuItem = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo, 1);
        ingredient.addMenuItem(menuItem);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ingredient.removeMenuItem(null);
        });
        assertTrue(exception.getMessage().contains("MenuItem cannot be null"));
    }

    @Test
    @DisplayName("1..*: Duplicate ingredient prevention")
    void testDuplicateIngredientPrevention() {
        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 50, 15, 5);
        MenuItem menuItem = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo, 1);
        Ingredient ingredient = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);

        menuItem.addIngredient(ingredient);
        menuItem.addIngredient(ingredient); // Adding same ingredient again

        assertEquals(1, menuItem.getIngredients().size());
        assertEquals(1, ingredient.getMenuItems().size());
    }

    @Test
    @DisplayName("1..*: Duplicate MenuItem prevention")
    void testDuplicateMenuItemPrevention() {
        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 50, 15, 5);
        MenuItem menuItem = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo, 1);
        Ingredient ingredient = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);

        ingredient.addMenuItem(menuItem);
        ingredient.addMenuItem(menuItem); // Adding same menu item again

        assertEquals(1, ingredient.getMenuItems().size());
        assertEquals(1, menuItem.getIngredients().size());
    }

    @Test
    @DisplayName("1..*: Bidirectional link established from either side")
    void testBidirectionalLinkFromEitherSide() {
        NutritionalInfo nutritionalInfo1 = new NutritionalInfo(500, 20, 50, 15, 5);
        NutritionalInfo nutritionalInfo2 = new NutritionalInfo(300, 10, 30, 10, 3);
        MenuItem menuItem1 = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo1, 1);
        MenuItem menuItem2 = new MainDish("Pizza", "Tasty pizza", 30.0, "pizza.jpg", "Italy", nutritionalInfo2, 2);
        Ingredient ingredient1 = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);
        Ingredient ingredient2 = new Ingredient("Basil", "kg", 5.0, 1.0, 10.0);

        // Add from MenuItem side
        menuItem1.addIngredient(ingredient1);
        assertEquals(menuItem1, ingredient1.getMenuItems().iterator().next());
        assertTrue(ingredient1.getMenuItems().contains(menuItem1));

        // Add from Ingredient side
        ingredient2.addMenuItem(menuItem2);
        assertEquals(ingredient2, menuItem2.getIngredients().iterator().next());
        assertTrue(menuItem2.getIngredients().contains(ingredient2));
    }

    @Test
    @DisplayName("1..*: Remove non-existent ingredient throws exception")
    void testRemoveNonExistentIngredient() {
        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 50, 15, 5);
        MenuItem menuItem = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo, 1);
        Ingredient ingredient1 = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);
        Ingredient ingredient2 = new Ingredient("Basil", "kg", 5.0, 1.0, 10.0);

        menuItem.addIngredient(ingredient1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuItem.removeIngredient(ingredient2);
        });
        assertTrue(exception.getMessage().contains("This ingredient is not part of this menu item"));
    }

    @Test
    @DisplayName("1..*: Remove non-existent MenuItem throws exception")
    void testRemoveNonExistentMenuItem() {
        NutritionalInfo nutritionalInfo1 = new NutritionalInfo(500, 20, 50, 15, 5);
        NutritionalInfo nutritionalInfo2 = new NutritionalInfo(300, 10, 30, 10, 3);
        MenuItem menuItem1 = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo1, 1);
        MenuItem menuItem2 = new MainDish("Pizza", "Tasty pizza", 30.0, "pizza.jpg", "Italy", nutritionalInfo2, 2);
        Ingredient ingredient = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);

        ingredient.addMenuItem(menuItem1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ingredient.removeMenuItem(menuItem2);
        });
        assertTrue(exception.getMessage().contains("This ingredient is not part of this menu item"));
    }

    @Test
    @DisplayName("1..*: Many-to-many relationship supported")
    void testManyToManyRelationship() {
        NutritionalInfo nutritionalInfo1 = new NutritionalInfo(500, 20, 50, 15, 5);
        NutritionalInfo nutritionalInfo2 = new NutritionalInfo(300, 10, 30, 10, 3);
        MenuItem menuItem1 = new MainDish("Pasta", "Delicious pasta", 25.0, "pasta.jpg", "Italy", nutritionalInfo1, 1);
        MenuItem menuItem2 = new MainDish("Pizza", "Tasty pizza", 30.0, "pizza.jpg", "Italy", nutritionalInfo2, 2);
        Ingredient ingredient1 = new Ingredient("Tomato", "kg", 10.0, 2.0, 5.0);
        Ingredient ingredient2 = new Ingredient("Basil", "kg", 5.0, 1.0, 10.0);

        // MenuItem1 uses both ingredients
        menuItem1.addIngredient(ingredient1);
        menuItem1.addIngredient(ingredient2);

        // MenuItem2 uses ingredient1
        menuItem2.addIngredient(ingredient1);

        // Verify MenuItem1 has 2 ingredients
        assertEquals(2, menuItem1.getIngredients().size());
        assertTrue(menuItem1.getIngredients().contains(ingredient1));
        assertTrue(menuItem1.getIngredients().contains(ingredient2));

        // Verify MenuItem2 has 1 ingredient
        assertEquals(1, menuItem2.getIngredients().size());
        assertTrue(menuItem2.getIngredients().contains(ingredient1));

        // Verify ingredient1 is used in 2 menu items
        assertEquals(2, ingredient1.getMenuItems().size());
        assertTrue(ingredient1.getMenuItems().contains(menuItem1));
        assertTrue(ingredient1.getMenuItems().contains(menuItem2));

        // Verify ingredient2 is used in 1 menu item
        assertEquals(1, ingredient2.getMenuItems().size());
        assertTrue(ingredient2.getMenuItems().contains(menuItem1));
    }
}
