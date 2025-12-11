import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class QualifiedAssociationTest {

    @BeforeEach
    void setup() {
        Customer.clearExtent();
        Reservation.clearExtent();
    }

    @Test
    @DisplayName("Qualified Association (1): Reservation requires Customer")
    void testReservationRequiresCustomer() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(19, 0);
        Table table = new Table(1, 4, "Section A");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Reservation(date, time, 4, null, table);
        });
        assertTrue(exception.getMessage().contains("Customer cannot be null"));
    }

    @Test
    @DisplayName("Qualified Association (1): Cannot set Customer to null")
    void testCannotSetCustomerToNull() {
        Customer customer = new Customer("John", "Doe", "john@test.com", "123456", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(19, 0);
        Table table = new Table(2, 4, "Section A");
        Reservation reservation = new Reservation(date, time, 4, customer, table);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservation.setCustomer(null);
        });
        assertTrue(exception.getMessage().contains("Customer cannot be null"));
    }

    @Test
    @DisplayName("Qualified Association: Add reservation to customer (Reverse Connection)")
    void testAddReservationToCustomer() {
        Customer customer = new Customer("John", "Doe", "john@test.com", "123456", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(19, 0);
        Table table = new Table(3, 4, "Section A");
        Reservation reservation = new Reservation(date, time, 4, customer, table);

        LocalDateTime key = LocalDateTime.of(date, time);
        assertEquals(reservation, customer.getReservation(key));
        assertEquals(customer, reservation.getCustomer());
    }

    @Test
    @DisplayName("Qualified Association: Set customer for reservation (Reverse Connection)")
    void testSetCustomerForReservation() {
        Customer customer1 = new Customer("Jane", "Doe", "jane@test.com", "654321", LocalDateTime.now());
        Customer customer2 = new Customer("Bob", "Smith", "bob@test.com", "111222", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(2);
        LocalTime time = LocalTime.of(20, 0);
        Table table = new Table(4, 2, "Section A");
        Reservation reservation = new Reservation(date, time, 2, customer1, table);

        // Change to a different customer
        reservation.setCustomer(customer2);

        assertEquals(customer2, reservation.getCustomer());
        assertEquals(reservation, customer2.getReservation(LocalDateTime.of(date, time)));
        assertNull(customer1.getReservation(LocalDateTime.of(date, time)));
    }

    @Test
    @DisplayName("Qualified Association (0..1): Duplicate Qualifier Restriction")
    void testDuplicateQualifier() {
        Customer customer = new Customer("John", "Doe", "john@test.com", "123456", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(19, 0);
        Table table1 = new Table(5, 4, "Section A");
        Table table2 = new Table(6, 2, "Section A");

        Reservation res1 = new Reservation(date, time, 4, customer, table1);

        // Customer can only have 0..1 Reservations at any given DateTime
        // Attempting to create a second reservation at the same date/time should throw an exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            new Reservation(date, time, 2, customer, table2);
        });

        assertTrue(exception.getMessage().contains("already has a reservation"));
        assertEquals(res1, customer.getReservation(LocalDateTime.of(date, time)));
    }

    @Test
    @DisplayName("Qualified Association: Remove reservation from customer")
    void testRemoveReservation() {
        Customer customer1 = new Customer("John", "Doe", "john@test.com", "123456", LocalDateTime.now());
        Customer customer2 = new Customer("Jane", "Smith", "jane@test.com", "987654", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(19, 0);
        Table table = new Table(7, 4, "Section A");
        Reservation reservation = new Reservation(date, time, 4, customer1, table);

        // Remove from customer1's map
        customer1.removeReservation(reservation);

        assertNull(customer1.getReservation(LocalDateTime.of(date, time)));
        // Reservation still has customer1 (cannot be null due to 1 multiplicity)
        assertEquals(customer1, reservation.getCustomer());

        // Can change to different customer after removal from map
        reservation.setCustomer(customer2);
        assertEquals(customer2, reservation.getCustomer());
        assertEquals(reservation, customer2.getReservation(LocalDateTime.of(date, time)));
    }

    @Test
    @DisplayName("Qualified Association: Update qualifier (date) - key should be updated in map")
    void testUpdateReservationDate() {
        Customer customer = new Customer("John", "Doe", "john@test.com", "123456", LocalDateTime.now());
        LocalDate oldDate = LocalDate.now().plusDays(1);
        LocalDate newDate = LocalDate.now().plusDays(2);
        LocalTime time = LocalTime.of(19, 0);
        Table table = new Table(8, 4, "Section A");

        Reservation reservation = new Reservation(oldDate, time, 4, customer, table);

        // Verify initial state
        LocalDateTime oldKey = LocalDateTime.of(oldDate, time);
        assertEquals(reservation, customer.getReservation(oldKey));

        // Change the date (qualifier)
        reservation.setDate(newDate);

        // Old key should no longer work
        assertNull(customer.getReservation(oldKey), "Old key should not retrieve the reservation");

        // New key should work
        LocalDateTime newKey = LocalDateTime.of(newDate, time);
        assertEquals(reservation, customer.getReservation(newKey), "New key should retrieve the reservation");

        // Customer association should still be intact
        assertEquals(customer, reservation.getCustomer());
    }

    @Test
    @DisplayName("Qualified Association: Update qualifier (time) - key should be updated in map")
    void testUpdateReservationTime() {
        Customer customer = new Customer("Jane", "Smith", "jane@test.com", "987654", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime oldTime = LocalTime.of(18, 0);
        LocalTime newTime = LocalTime.of(20, 0);
        Table table = new Table(9, 2, "Section A");

        Reservation reservation = new Reservation(date, oldTime, 2, customer, table);

        // Verify initial state
        LocalDateTime oldKey = LocalDateTime.of(date, oldTime);
        assertEquals(reservation, customer.getReservation(oldKey));

        // Change the time (qualifier)
        reservation.setTime(newTime);

        // Old key should no longer work
        assertNull(customer.getReservation(oldKey), "Old key should not retrieve the reservation");

        // New key should work
        LocalDateTime newKey = LocalDateTime.of(date, newTime);
        assertEquals(reservation, customer.getReservation(newKey), "New key should retrieve the reservation");

        // Customer association should still be intact
        assertEquals(customer, reservation.getCustomer());
    }

    @Test
    @DisplayName("Qualified Association: Update both date and time - key should be updated correctly")
    void testUpdateReservationDateAndTime() {
        Customer customer = new Customer("Bob", "Jones", "bob@test.com", "555123", LocalDateTime.now());
        LocalDate oldDate = LocalDate.now().plusDays(1);
        LocalTime oldTime = LocalTime.of(18, 0);
        LocalDate newDate = LocalDate.now().plusDays(3);
        LocalTime newTime = LocalTime.of(21, 0);
        Table table = new Table(10, 3, "Section A");

        Reservation reservation = new Reservation(oldDate, oldTime, 3, customer, table);

        // Change date first
        reservation.setDate(newDate);

        // Change time second
        reservation.setTime(newTime);

        // Old key should not work
        LocalDateTime oldKey = LocalDateTime.of(oldDate, oldTime);
        assertNull(customer.getReservation(oldKey));

        // New key should work
        LocalDateTime newKey = LocalDateTime.of(newDate, newTime);
        assertEquals(reservation, customer.getReservation(newKey));

        // Customer association should still be intact
        assertEquals(customer, reservation.getCustomer());
    }

    @Test
    @DisplayName("Qualified Association: Cannot update qualifier to existing reservation time")
    void testCannotUpdateToExistingQualifier() {
        Customer customer = new Customer("Alice", "Brown", "alice@test.com", "999888", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time1 = LocalTime.of(18, 0);
        LocalTime time2 = LocalTime.of(19, 0);
        Table table1 = new Table(11, 4, "Section A");
        Table table2 = new Table(12, 2, "Section B");

        // Create two reservations at different times
        Reservation res1 = new Reservation(date, time1, 4, customer, table1);
        Reservation res2 = new Reservation(date, time2, 2, customer, table2);

        // Verify both exist
        assertEquals(res1, customer.getReservation(LocalDateTime.of(date, time1)));
        assertEquals(res2, customer.getReservation(LocalDateTime.of(date, time2)));

        // Try to change res2's time to res1's time - should throw exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            res2.setTime(time1);
        });

        assertTrue(exception.getMessage().contains("already has a reservation"));

        // Verify res2's time is unchanged
        assertEquals(time2, res2.getTime());
        assertEquals(res2, customer.getReservation(LocalDateTime.of(date, time2)));
        assertEquals(res1, customer.getReservation(LocalDateTime.of(date, time1)));
    }

    @Test
    @DisplayName("Qualified Association: Delete reservation completely")
    void testDeleteReservation() {
        Customer customer = new Customer("Mike", "Wilson", "mike@test.com", "777888", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(19, 0);
        Table table = new Table(13, 4, "Section A");

        int initialExtentSize = Reservation.getAllReservationsFromExtent().size();

        Reservation reservation = new Reservation(date, time, 4, customer, table);

        // Verify reservation exists everywhere
        assertEquals(reservation, customer.getReservation(LocalDateTime.of(date, time)));
        assertTrue(table.getReservations().contains(reservation));
        assertEquals(initialExtentSize + 1, Reservation.getAllReservationsFromExtent().size());

        // Delete the reservation
        customer.deleteReservation(reservation);

        // Verify reservation is removed from everywhere
        assertNull(customer.getReservation(LocalDateTime.of(date, time)));
        assertFalse(table.getReservations().contains(reservation));
        assertEquals(initialExtentSize, Reservation.getAllReservationsFromExtent().size());
    }

    @Test
    @DisplayName("Qualified Association: Cannot delete reservation that doesn't belong to customer")
    void testCannotDeleteOtherCustomerReservation() {
        Customer customer1 = new Customer("Tom", "Brown", "tom@test.com", "111222", LocalDateTime.now());
        Customer customer2 = new Customer("Sarah", "Green", "sarah@test.com", "333444", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(19, 0);
        Table table = new Table(14, 4, "Section A");

        Reservation reservation = new Reservation(date, time, 4, customer1, table);

        // customer2 tries to delete customer1's reservation
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            customer2.deleteReservation(reservation);
        });

        assertTrue(exception.getMessage().contains("does not belong to this customer"));

        // Verify reservation still exists
        assertEquals(reservation, customer1.getReservation(LocalDateTime.of(date, time)));
        assertTrue(table.getReservations().contains(reservation));
    }

    @Test
    @DisplayName("Qualified Association: Customer can have multiple reservations at different times")
    void testMultipleReservationsAtDifferentTimes() {
        Customer customer = new Customer("Emma", "Davis", "emma@test.com", "555666", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        Table table1 = new Table(15, 4, "Section A");
        Table table2 = new Table(16, 2, "Section B");
        Table table3 = new Table(17, 6, "Section C");

        // Create 3 reservations at different times
        Reservation res1 = new Reservation(date, LocalTime.of(18, 0), 4, customer, table1);
        Reservation res2 = new Reservation(date, LocalTime.of(19, 0), 2, customer, table2);
        Reservation res3 = new Reservation(date, LocalTime.of(20, 0), 6, customer, table3);

        // Customer should have all 3 reservations
        assertEquals(3, customer.getReservations().size());
        assertEquals(res1, customer.getReservation(LocalDateTime.of(date, LocalTime.of(18, 0))));
        assertEquals(res2, customer.getReservation(LocalDateTime.of(date, LocalTime.of(19, 0))));
        assertEquals(res3, customer.getReservation(LocalDateTime.of(date, LocalTime.of(20, 0))));
    }

    @Test
    @DisplayName("Qualified Association: Cannot transfer to customer with conflicting reservation")
    void testCannotTransferToCustomerWithConflict() {
        Customer customer1 = new Customer("Frank", "Miller", "frank@test.com", "111333", LocalDateTime.now());
        Customer customer2 = new Customer("Grace", "Lee", "grace@test.com", "222444", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(19, 0);
        Table table1 = new Table(18, 4, "Section A");
        Table table2 = new Table(19, 2, "Section B");

        // Both customers have reservations at the same date/time
        Reservation res1 = new Reservation(date, time, 4, customer1, table1);
        Reservation res2 = new Reservation(date, time, 2, customer2, table2);

        // Try to transfer res1 to customer2 (who already has res2 at that time)
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            res1.setCustomer(customer2);
        });

        assertTrue(exception.getMessage().contains("already has a reservation"));

        // Verify res1 is still with customer1 (no orphaned reservation)
        assertEquals(customer1, res1.getCustomer());
        assertEquals(res1, customer1.getReservation(LocalDateTime.of(date, time)));

        // Verify res2 is still with customer2 (unchanged)
        assertEquals(customer2, res2.getCustomer());
        assertEquals(res2, customer2.getReservation(LocalDateTime.of(date, time)));  // Should still be res2, not res1
    }
}
