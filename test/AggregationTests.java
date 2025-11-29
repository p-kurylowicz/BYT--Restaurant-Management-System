import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Unit tests for Aggregation associations:
 * 1. Table ◇—— Reservation (0..1 to 0..*)
 * 2. Ingredient ◇—— Supplier (1..* to 0..*)
 */
public class AggregationTests {

    @BeforeEach
    public void setup() {
        Table.clearExtent();
        Reservation.clearExtent();
        Ingredient.clearExtent();
        Supplier.clearExtent();
    }

    @Test
    @DisplayName("Table-Reservation: Add reservation to table")
    public void testTableAddReservation() {
        // Arrange
        Table table = new Table(1, 4, "Main");
        Reservation reservation = new Reservation(LocalDate.now().plusDays(1), LocalTime.of(18, 0), 4);
        
        table.addReservation(reservation);
        
        assertTrue(table.getReservations().contains(reservation), "Table should contain the reservation");
        assertEquals(table, reservation.getAssignedTable(), "Reservation should reference the table (reverse connection)");
        assertEquals(1, table.getReservations().size(), "Table should have 1 reservation");
    }
    
    @Test
    @DisplayName("Table-Reservation: Remove reservation from table")
    public void testTableRemoveReservation() {
        Table table = new Table(2, 6, "Patio");
        Reservation reservation = new Reservation(LocalDate.now().plusDays(2), LocalTime.of(19, 0), 6);
        table.addReservation(reservation);
        
        table.removeReservation(reservation);
        
        assertFalse(table.getReservations().contains(reservation), "Table should not contain the reservation");
        assertNull(reservation.getAssignedTable(), "Reservation should not reference any table (reverse connection cleared)");
        assertEquals(0, table.getReservations().size(), "Table should have 0 reservations");
    }
    
    @Test
    @DisplayName("Table-Reservation: Assign table from reservation side")
    public void testReservationAssignTable() {
        Table table = new Table(3, 2, "Bar");
        Reservation reservation = new Reservation(LocalDate.now().plusDays(3), LocalTime.of(20, 0), 2);
        
        reservation.assignTable(table);
        
        assertEquals(table, reservation.getAssignedTable(), "Reservation should reference the table");
        assertTrue(table.getReservations().contains(reservation), "Table should contain the reservation (reverse connection)");
    }
    
    @Test
    @DisplayName("Table-Reservation: Remove table from reservation side")
    public void testReservationRemoveTable() {
        // Arrange
        Table table = new Table(4, 8, "VIP");
        Reservation reservation = new Reservation(LocalDate.now().plusDays(4), LocalTime.of(21, 0), 8);
        reservation.assignTable(table);
        
        // Act
        reservation.removeTable();
        
        // Assert
        assertNull(reservation.getAssignedTable(), "Reservation should not reference any table");
        assertFalse(table.getReservations().contains(reservation), "Table should not contain the reservation (reverse connection cleared)");
    }
    
    @Test
    @DisplayName("Table-Reservation: Modify table assignment (reassign to different table)")
    public void testReservationReassignTable() {
        Table table1 = new Table(5, 4, "Main");
        Table table2 = new Table(6, 4, "Patio");
        Reservation reservation = new Reservation(LocalDate.now().plusDays(5), LocalTime.of(18, 30), 4);
        reservation.assignTable(table1);
        
        reservation.assignTable(table2);
        
        assertEquals(table2, reservation.getAssignedTable(), "Reservation should reference new table");
        assertFalse(table1.getReservations().contains(reservation), "Old table should not contain reservation");
        assertTrue(table2.getReservations().contains(reservation), "New table should contain reservation");
    }
    
    @Test
    @DisplayName("Table-Reservation: Table can exist without reservations (0..* multiplicity)")
    public void testTableCanExistWithoutReservations() {
        Table table = new Table(7, 4, "Main");
        
        assertEquals(0, table.getReservations().size(), "Table can exist with 0 reservations");
        assertNotNull(table, "Table should be created successfully");
    }
    
    @Test
    @DisplayName("Table-Reservation: Reservation can exist without table (0..1 multiplicity)")
    public void testReservationCanExistWithoutTable() {
        Reservation reservation = new Reservation(LocalDate.now().plusDays(1), LocalTime.of(19, 0), 4);
        
        assertNull(reservation.getAssignedTable(), "Reservation can exist without table");
        assertNotNull(reservation, "Reservation should be created successfully");
    }
    
    // ==================== ERROR HANDLING: TABLE-RESERVATION ====================
    
    @Test
    @DisplayName("Table-Reservation: Error - Add null reservation")
    public void testTableAddNullReservation() {
        Table table = new Table(8, 4, "Main");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            table.addReservation(null);
        });
        assertEquals("Reservation cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Table-Reservation: Error - Add duplicate reservation")
    public void testTableAddDuplicateReservation() {
        Table table = new Table(9, 4, "Main");
        Reservation reservation = new Reservation(LocalDate.now().plusDays(1), LocalTime.of(18, 0), 4);
        table.addReservation(reservation);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            table.addReservation(reservation);
        });
        assertEquals("This reservation is already assigned to this table", exception.getMessage());
    }
    
    @Test
    @DisplayName("Table-Reservation: Error - Remove non-existent reservation")
    public void testTableRemoveNonExistentReservation() {
        Table table = new Table(10, 4, "Main");
        Reservation reservation = new Reservation(LocalDate.now().plusDays(1), LocalTime.of(18, 0), 4);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            table.removeReservation(reservation);
        });
        assertEquals("This reservation is not assigned to this table", exception.getMessage());
    }
    
    @Test
    @DisplayName("Table-Reservation: Error - Assign null table")
    public void testReservationAssignNullTable() {
        Reservation reservation = new Reservation(LocalDate.now().plusDays(1), LocalTime.of(18, 0), 4);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservation.assignTable(null);
        });
        assertEquals("Table cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Table-Reservation: Error - Table capacity insufficient")
    public void testTableCapacityInsufficient() {
        Table table = new Table(11, 2, "Bar");
        Reservation reservation = new Reservation(LocalDate.now().plusDays(1), LocalTime.of(18, 0), 4);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservation.assignTable(table);
        });
        assertTrue(exception.getMessage().contains("insufficient for party size"));
    }
    
    // ==================== INGREDIENT-SUPPLIER AGGREGATION TESTS ====================
    
    @Test
    @DisplayName("Ingredient-Supplier: Add supplier to ingredient")
    public void testIngredientAddSupplier() {
        Ingredient ingredient = new Ingredient("Tomato", "kg", 10.0, 5.0, 2.5);
        Supplier supplier = new Supplier("Fresh Farm", "555-1234", "farm@email.com", "123 Farm Rd", 4.5, "John");
        
        ingredient.addSupplier(supplier);
        
        assertTrue(ingredient.getSuppliers().contains(supplier), "Ingredient should contain the supplier");
        assertTrue(supplier.getIngredients().contains(ingredient), "Supplier should contain the ingredient (reverse connection)");
        assertEquals(1, ingredient.getSuppliers().size(), "Ingredient should have 1 supplier");
    }
    
    @Test
    @DisplayName("Ingredient-Supplier: Remove supplier from ingredient (with multiple suppliers)")
    public void testIngredientRemoveSupplier() {
        Ingredient ingredient = new Ingredient("Chicken", "kg", 15.0, 8.0, 8.0);
        Supplier supplier1 = new Supplier("Poultry Co", "555-1111", "poultry@email.com", "456 Farm Rd", 4.0, "Jane");
        Supplier supplier2 = new Supplier("Fresh Meat", "555-2222", "meat@email.com", "789 Market St", 4.8, "Bob");
        ingredient.addSupplier(supplier1);
        ingredient.addSupplier(supplier2);
        
        ingredient.removeSupplier(supplier1);
        
        assertFalse(ingredient.getSuppliers().contains(supplier1), "Ingredient should not contain supplier1");
        assertFalse(supplier1.getIngredients().contains(ingredient), "Supplier1 should not contain ingredient (reverse connection cleared)");
        assertTrue(ingredient.getSuppliers().contains(supplier2), "Ingredient should still contain supplier2");
        assertEquals(1, ingredient.getSuppliers().size(), "Ingredient should have 1 supplier");
    }
    
    @Test
    @DisplayName("Ingredient-Supplier: Add ingredient from supplier side")
    public void testSupplierAddIngredient() {
        Supplier supplier = new Supplier("Veggie Market", "555-3333", "veggie@email.com", "321 Market St", 4.2, "Alice");
        Ingredient ingredient = new Ingredient("Lettuce", "kg", 5.0, 2.0, 1.5);
        
        supplier.addIngredient(ingredient);
        
        assertTrue(supplier.getIngredients().contains(ingredient), "Supplier should contain the ingredient");
        assertTrue(ingredient.getSuppliers().contains(supplier), "Ingredient should contain the supplier (reverse connection)");
    }
    
    @Test
    @DisplayName("Ingredient-Supplier: Remove ingredient from supplier side")
    public void testSupplierRemoveIngredient() {
        Supplier supplier1 = new Supplier("Dairy Farm", "555-4444", "dairy@email.com", "111 Farm Rd", 4.7, "Mike");
        Supplier supplier2 = new Supplier("Milk Co", "555-5555", "milk@email.com", "222 Dairy Ln", 4.3, "Sarah");
        Ingredient ingredient = new Ingredient("Milk", "liter", 20.0, 10.0, 1.2);
        supplier1.addIngredient(ingredient);
        supplier2.addIngredient(ingredient);
        
        supplier1.removeIngredient(ingredient);
        
        assertFalse(supplier1.getIngredients().contains(ingredient), "Supplier1 should not contain ingredient");
        assertFalse(ingredient.getSuppliers().contains(supplier1), "Ingredient should not contain supplier1 (reverse connection cleared)");
        assertTrue(supplier2.getIngredients().contains(ingredient), "Supplier2 should still contain ingredient");
    }
    
    @Test
    @DisplayName("Ingredient-Supplier: Replace supplier")
    public void testIngredientReplaceSupplier() {
        Ingredient ingredient = new Ingredient("Olive Oil", "liter", 8.0, 3.0, 12.0);
        Supplier oldSupplier = new Supplier("Oil Co A", "555-6666", "oila@email.com", "333 Oil St", 3.5, "Tom");
        Supplier newSupplier = new Supplier("Oil Co B", "555-7777", "oilb@email.com", "444 Oil Ave", 4.9, "Emma");
        ingredient.addSupplier(oldSupplier);
        
        ingredient.replaceSupplier(oldSupplier, newSupplier);
        
        assertFalse(ingredient.getSuppliers().contains(oldSupplier), "Ingredient should not contain old supplier");
        assertTrue(ingredient.getSuppliers().contains(newSupplier), "Ingredient should contain new supplier");
        assertFalse(oldSupplier.getIngredients().contains(ingredient), "Old supplier should not contain ingredient");
        assertTrue(newSupplier.getIngredients().contains(ingredient), "New supplier should contain ingredient");
        assertEquals(1, ingredient.getSuppliers().size(), "Ingredient should have 1 supplier");
    }
    
    @Test
    @DisplayName("Ingredient-Supplier: Supplier can exist without ingredients (0..* multiplicity)")
    public void testSupplierCanExistWithoutIngredients() {
        Supplier supplier = new Supplier("New Supplier", "555-8888", "new@email.com", "555 New St", 4.0, "Chris");
        
        assertEquals(0, supplier.getIngredients().size(), "Supplier can exist with 0 ingredients");
        assertNotNull(supplier, "Supplier should be created successfully");
    }
    
    @Test
    @DisplayName("Ingredient-Supplier: Get primary supplier (highest rating)")
    public void testGetPrimarySupplier() {
        Ingredient ingredient = new Ingredient("Beef", "kg", 25.0, 10.0, 15.0);
        Supplier supplier1 = new Supplier("Butcher A", "555-9999", "butchera@email.com", "666 Meat St", 4.2, "Dave");
        Supplier supplier2 = new Supplier("Butcher B", "555-0000", "butcherb@email.com", "777 Meat Ave", 4.9, "Lisa");
        Supplier supplier3 = new Supplier("Butcher C", "555-1212", "butcherc@email.com", "888 Meat Rd", 4.5, "Paul");
        ingredient.addSupplier(supplier1);
        ingredient.addSupplier(supplier2);
        ingredient.addSupplier(supplier3);
        
        Supplier primary = ingredient.getPrimarySupplier();
        
        assertEquals(supplier2, primary, "Primary supplier should be the one with highest rating (4.9)");
    }
    
    // ==================== ERROR HANDLING: INGREDIENT-SUPPLIER ====================
    
    @Test
    @DisplayName("Ingredient-Supplier: Error - Add null supplier")
    public void testIngredientAddNullSupplier() {
        Ingredient ingredient = new Ingredient("Flour", "kg", 50.0, 20.0, 1.0);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ingredient.addSupplier(null);
        });
        assertEquals("Supplier cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Ingredient-Supplier: Error - Add duplicate supplier")
    public void testIngredientAddDuplicateSupplier() {
        Ingredient ingredient = new Ingredient("Sugar", "kg", 30.0, 15.0, 0.8);
        Supplier supplier = new Supplier("Sweet Co", "555-3434", "sweet@email.com", "999 Sugar Ln", 4.1, "Amy");
        ingredient.addSupplier(supplier);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ingredient.addSupplier(supplier);
        });
        assertEquals("This supplier is already associated with this ingredient", exception.getMessage());
    }
    
    @Test
    @DisplayName("Ingredient-Supplier: Error - Remove last supplier (violates 1..* multiplicity)")
    public void testIngredientRemoveLastSupplier() {
        Ingredient ingredient = new Ingredient("Salt", "kg", 40.0, 20.0, 0.5);
        Supplier supplier = new Supplier("Salt Mine", "555-5656", "salt@email.com", "100 Mine Rd", 4.6, "Steve");
        ingredient.addSupplier(supplier);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ingredient.removeSupplier(supplier);
        });
        assertEquals("Cannot remove supplier: ingredient must have at least one supplier", exception.getMessage());
    }
    
    @Test
    @DisplayName("Ingredient-Supplier: Error - Remove non-existent supplier")
    public void testIngredientRemoveNonExistentSupplier() {
        Ingredient ingredient = new Ingredient("Pepper", "kg", 10.0, 5.0, 3.0);
        Supplier supplier1 = new Supplier("Spice Co A", "555-7878", "spicea@email.com", "200 Spice St", 4.0, "Nina");
        Supplier supplier2 = new Supplier("Spice Co B", "555-9090", "spiceb@email.com", "300 Spice Ave", 4.2, "Oscar");
        ingredient.addSupplier(supplier1);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ingredient.removeSupplier(supplier2);
        });
        assertEquals("This supplier is not associated with this ingredient", exception.getMessage());
    }
    
    @Test
    @DisplayName("Ingredient-Supplier: Error - Supplier remove ingredient when it's the last supplier")
    public void testSupplierRemoveIngredientViolatesMultiplicity() {
        Supplier supplier = new Supplier("Only Supplier", "555-1313", "only@email.com", "400 Solo St", 4.5, "Rick");
        Ingredient ingredient = new Ingredient("Rare Spice", "kg", 2.0, 1.0, 20.0);
        supplier.addIngredient(ingredient);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            supplier.removeIngredient(ingredient);
        });
        assertTrue(exception.getMessage().contains("violates 1..* constraint"));
    }
    
    @Test
    @DisplayName("Ingredient-Supplier: Error - Replace with null supplier")
    public void testIngredientReplaceWithNullSupplier() {
        Ingredient ingredient = new Ingredient("Garlic", "kg", 8.0, 4.0, 2.0);
        Supplier supplier = new Supplier("Garlic Farm", "555-1414", "garlic@email.com", "500 Garlic Rd", 4.3, "Helen");
        ingredient.addSupplier(supplier);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ingredient.replaceSupplier(supplier, null);
        });
        assertEquals("Suppliers cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Ingredient-Supplier: Error - Replace non-existent old supplier")
    public void testIngredientReplaceNonExistentSupplier() {
        Ingredient ingredient = new Ingredient("Onion", "kg", 15.0, 8.0, 1.2);
        Supplier supplier1 = new Supplier("Onion Farm A", "555-1515", "oniona@email.com", "600 Onion St", 4.0, "Greg");
        Supplier supplier2 = new Supplier("Onion Farm B", "555-1616", "onionb@email.com", "700 Onion Ave", 4.4, "Fiona");
        ingredient.addSupplier(supplier1);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ingredient.replaceSupplier(supplier2, new Supplier("New Farm", "555-1717", "new@email.com", "800 Farm Rd", 4.5, "Ian"));
        });
        assertEquals("Old supplier is not associated with this ingredient", exception.getMessage());
    }
}
