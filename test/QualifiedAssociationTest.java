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
}
