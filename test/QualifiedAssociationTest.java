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
    @DisplayName("Qualified Association: Add reservation to customer (Reverse Connection)")
    void testAddReservationToCustomer() {
        Customer customer = new Customer("John", "Doe", "john@test.com", "123456", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(19, 0);
        Reservation reservation = new Reservation(date, time, 4);

        customer.addReservation(reservation);

        LocalDateTime key = LocalDateTime.of(date, time);
        assertEquals(reservation, customer.getReservation(key));
        assertEquals(customer, reservation.getCustomer());
    }

    @Test
    @DisplayName("Qualified Association: Set customer for reservation (Reverse Connection)")
    void testSetCustomerForReservation() {
        Customer customer = new Customer("Jane", "Doe", "jane@test.com", "654321", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(2);
        LocalTime time = LocalTime.of(20, 0);
        Reservation reservation = new Reservation(date, time, 2);

        reservation.setCustomer(customer);

        assertEquals(customer, reservation.getCustomer());
        assertEquals(reservation, customer.getReservation(LocalDateTime.of(date, time)));
    }

    @Test
    @DisplayName("Qualified Association: Duplicate Qualifier Restriction")
    void testDuplicateQualifier() {
        Customer customer = new Customer("John", "Doe", "john@test.com", "123456", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(19, 0);
        
        Reservation res1 = new Reservation(date, time, 4);
        Reservation res2 = new Reservation(date, time, 2);

        customer.addReservation(res1);
        
        customer.addReservation(res2);

        assertEquals(res1, customer.getReservation(LocalDateTime.of(date, time)));
        assertNotEquals(res2, customer.getReservation(LocalDateTime.of(date, time)));
    }
    
    @Test
    @DisplayName("Qualified Association: Remove reservation")
    void testRemoveReservation() {
        Customer customer = new Customer("John", "Doe", "john@test.com", "123456", LocalDateTime.now());
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(19, 0);
        Reservation reservation = new Reservation(date, time, 4);

        customer.addReservation(reservation);

        customer.removeReservation(reservation);

        assertNull(customer.getReservation(LocalDateTime.of(date, time)));
        assertNull(reservation.getCustomer());
    }

    @Test
    @DisplayName("Qualified Association: Update qualifier (date) - key should be updated in map")
    void testUpdateReservationDate() {
        Customer customer = new Customer("John", "Doe", "john@test.com", "123456", LocalDateTime.now());
        LocalDate oldDate = LocalDate.now().plusDays(1);
        LocalDate newDate = LocalDate.now().plusDays(2);
        LocalTime time = LocalTime.of(19, 0);

        Reservation reservation = new Reservation(oldDate, time, 4);
        customer.addReservation(reservation);

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

        Reservation reservation = new Reservation(date, oldTime, 2);
        customer.addReservation(reservation);

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

        Reservation reservation = new Reservation(oldDate, oldTime, 3);
        customer.addReservation(reservation);

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
}
