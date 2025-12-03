import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Unit tests for Aggregation associations:
 * 1. Table ◇—— Reservation (0..1 to 0..*)
 */
public class AggregationTests {

    @BeforeEach
    public void setup() {
        Table.clearExtent();
        Reservation.clearExtent();
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
}
