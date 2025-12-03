import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AggregationAssociationTest {

    @BeforeEach
    void setup() {
        Ingredient.clearExtent();
        Supplier.clearExtent();
    }

    @Test
    @DisplayName("Aggregation: Adding supplier establishes reverse connection")
    void testAddSupplierEstablishesReverseConnection() {
        Ingredient ingredient = new Ingredient("Tomato", "kg", 100, 50, 5.0);
        Supplier supplier = new Supplier("Farm Fresh", "123", "farm@test.com", "Farm Road", 4.5, "John");

        ingredient.addSupplier(supplier);

        assertTrue(ingredient.getSuppliers().contains(supplier));
        assertTrue(supplier.getIngredients().contains(ingredient));
    }

    @Test
    @DisplayName("Aggregation: Adding ingredient establishes reverse connection")
    void testAddIngredientEstablishesReverseConnection() {
        Ingredient ingredient = new Ingredient("Carrot", "kg", 50, 20, 3.0);
        Supplier supplier = new Supplier("Fresh Foods", "456", "fresh@test.com", "Food St", 4.0, "Jane");

        supplier.addIngredient(ingredient);

        assertTrue(supplier.getIngredients().contains(ingredient));
        assertTrue(ingredient.getSuppliers().contains(supplier));
    }

    @Test
    @DisplayName("Aggregation: Ingredient must have at least one supplier (1..*)")
    void testIngredientMinimumMultiplicity() {
        Ingredient ingredient = new Ingredient("Onion", "kg", 40, 20, 2.5);
        Supplier supplier = new Supplier("Veggie Ltd", "789", "veggie@test.com", "Veg St", 3.5, "Alice");

        ingredient.addSupplier(supplier);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ingredient.removeSupplier(supplier);
        });
        assertTrue(exception.getMessage().contains("ingredient must have at least one supplier"));
    }

    @Test
    @DisplayName("Aggregation: Supplier can exist without ingredients (0..*)")
    void testSupplierCanExistWithoutIngredients() {
        Supplier supplier = new Supplier("Empty Supplier", "111", "empty@test.com", "Empty St", 4.0, "Bob");

        assertNotNull(supplier);
        assertEquals(0, supplier.getIngredients().size());
    }

    @Test
    @DisplayName("Aggregation: Ingredient can be shared between multiple suppliers")
    void testIngredientSharedBetweenSuppliers() {
        Ingredient ingredient = new Ingredient("Potato", "kg", 80, 40, 1.5);
        Supplier supplier1 = new Supplier("Farm A", "111", "farmA@test.com", "Farm A Rd", 4.5, "John");
        Supplier supplier2 = new Supplier("Farm B", "222", "farmB@test.com", "Farm B Rd", 4.2, "Jane");
        Supplier supplier3 = new Supplier("Farm C", "333", "farmC@test.com", "Farm C Rd", 4.8, "Mike");

        ingredient.addSupplier(supplier1);
        ingredient.addSupplier(supplier2);
        ingredient.addSupplier(supplier3);

        assertEquals(3, ingredient.getSuppliers().size());
        assertTrue(ingredient.getSuppliers().contains(supplier1));
        assertTrue(ingredient.getSuppliers().contains(supplier2));
        assertTrue(ingredient.getSuppliers().contains(supplier3));
    }

    @Test
    @DisplayName("Aggregation: Removing supplier removes reverse connection")
    void testRemoveSupplierRemovesReverseConnection() {
        Ingredient ingredient = new Ingredient("Lettuce", "kg", 30, 15, 4.0);
        Supplier supplier1 = new Supplier("Supplier 1", "111", "s1@test.com", "S1 St", 4.0, "A");
        Supplier supplier2 = new Supplier("Supplier 2", "222", "s2@test.com", "S2 St", 4.5, "B");
        Supplier supplier3 = new Supplier("Supplier 3", "333", "s3@test.com", "S3 St", 4.2, "C");

        ingredient.addSupplier(supplier1);
        ingredient.addSupplier(supplier2);
        ingredient.addSupplier(supplier3);

        supplier1.removeIngredient(ingredient);

        assertFalse(ingredient.getSuppliers().contains(supplier1));
        assertFalse(supplier1.getIngredients().contains(ingredient));
        assertTrue(ingredient.getSuppliers().contains(supplier2));
        assertTrue(ingredient.getSuppliers().contains(supplier3));
    }

    @Test
    @DisplayName("Aggregation: Supplier can remove ingredient without violating constraints")
    void testSupplierRemoveIngredient() {
        Ingredient ingredient = new Ingredient("Pepper", "kg", 20, 10, 8.0);
        Supplier supplier1 = new Supplier("Supplier A", "111", "sA@test.com", "SA St", 4.0, "X");
        Supplier supplier2 = new Supplier("Supplier B", "222", "sB@test.com", "SB St", 4.5, "Y");

        ingredient.addSupplier(supplier1);
        ingredient.addSupplier(supplier2);

        supplier1.removeIngredient(ingredient);

        assertFalse(supplier1.getIngredients().contains(ingredient));
        assertFalse(ingredient.getSuppliers().contains(supplier1));
        assertTrue(ingredient.getSuppliers().contains(supplier2));
    }

    @Test
    @DisplayName("Aggregation: Cannot remove supplier if it violates ingredient's 1..* constraint")
    void testCannotRemoveSupplierViolatingConstraint() {
        Ingredient ingredient = new Ingredient("Garlic", "kg", 15, 5, 12.0);
        Supplier supplier = new Supplier("Only Supplier", "999", "only@test.com", "Only St", 5.0, "Z");

        supplier.addIngredient(ingredient);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            supplier.removeIngredient(ingredient);
        });
        assertTrue(exception.getMessage().contains("would leave the ingredient without any suppliers"));
    }

    @Test
    @DisplayName("Aggregation: Replace supplier functionality")
    void testReplaceSupplier() {
        Ingredient ingredient = new Ingredient("Cucumber", "kg", 25, 10, 3.5);
        Supplier oldSupplier = new Supplier("Old Supplier", "111", "old@test.com", "Old St", 3.5, "A");
        Supplier tempSupplier = new Supplier("Temp Supplier", "999", "temp@test.com", "Temp St", 4.0, "T");
        Supplier newSupplier = new Supplier("New Supplier", "222", "new@test.com", "New St", 4.8, "B");

        ingredient.addSupplier(oldSupplier);
        ingredient.addSupplier(tempSupplier);

        ingredient.replaceSupplier(oldSupplier, newSupplier);

        assertFalse(ingredient.getSuppliers().contains(oldSupplier));
        assertTrue(ingredient.getSuppliers().contains(newSupplier));
        assertTrue(ingredient.getSuppliers().contains(tempSupplier));
        assertFalse(oldSupplier.getIngredients().contains(ingredient));
        assertTrue(newSupplier.getIngredients().contains(ingredient));
    }

    @Test
    @DisplayName("Aggregation: Cannot add duplicate supplier")
    void testCannotAddDuplicateSupplier() {
        Ingredient ingredient = new Ingredient("Mushroom", "kg", 10, 5, 15.0);
        Supplier supplier = new Supplier("Supplier", "123", "sup@test.com", "Sup St", 4.0, "S");

        ingredient.addSupplier(supplier);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ingredient.addSupplier(supplier);
        });
        assertTrue(exception.getMessage().contains("already associated"));
    }

    @Test
    @DisplayName("Aggregation: Cannot add null supplier")
    void testCannotAddNullSupplier() {
        Ingredient ingredient = new Ingredient("Spinach", "kg", 20, 10, 6.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ingredient.addSupplier(null);
        });
        assertTrue(exception.getMessage().contains("Supplier cannot be null"));
    }

    @Test
    @DisplayName("Aggregation: Supplier can clear ingredients respecting constraints")
    void testSupplierClearIngredients() {
        Ingredient ingredient1 = new Ingredient("Apple", "kg", 50, 20, 4.0);
        Ingredient ingredient2 = new Ingredient("Banana", "kg", 40, 15, 3.5);
        Supplier supplier1 = new Supplier("Supplier 1", "111", "s1@test.com", "S1", 4.0, "A");
        Supplier supplier2 = new Supplier("Supplier 2", "222", "s2@test.com", "S2", 4.5, "B");

        ingredient1.addSupplier(supplier1);
        ingredient1.addSupplier(supplier2);
        ingredient2.addSupplier(supplier1);
        ingredient2.addSupplier(supplier2);

        supplier1.clearIngredients();

        assertEquals(0, supplier1.getIngredients().size());
        assertEquals(1, ingredient1.getSuppliers().size());
        assertEquals(1, ingredient2.getSuppliers().size());
    }
}
