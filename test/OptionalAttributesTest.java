import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Unit tests for Optional Attributes
 * Tests attributes that can be null, like Reservation.specialRequests
 */
public class OptionalAttributesTest {

    @BeforeEach
    void clearExtents() {
        Customer.clearExtent();
        Reservation.clearExtent();
    }

    @Test
    @DisplayName("Optional attribute can be null by default")
    void testOptionalAttributeDefaultNull() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(customer,
            LocalDate.now().plusDays(7), LocalTime.of(18, 30), 2);

        assertNull(reservation.getSpecialRequests(),
            "Optional attribute should be null by default");
    }

    @Test
    @DisplayName("Optional attribute can have a value")
    void testOptionalAttributeWithValue() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(customer,
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4);
        reservation.setSpecialRequests("Window seat, birthday celebration");

        assertNotNull(reservation.getSpecialRequests());
        assertEquals("Window seat, birthday celebration", reservation.getSpecialRequests());
    }

    @Test
    @DisplayName("Optional attribute empty string converts to null")
    void testOptionalAttributeEmptyStringToNull() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(customer,
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4);

        reservation.setSpecialRequests("Some request");
        assertNotNull(reservation.getSpecialRequests());

        // Set to empty string
        reservation.setSpecialRequests("");
        assertNull(reservation.getSpecialRequests(),
            "Empty string should convert to null for optional attribute");
    }

    @Test
    @DisplayName("Optional attribute whitespace-only string converts to null")
    void testOptionalAttributeWhitespaceToNull() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(customer,
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4);

        reservation.setSpecialRequests("   ");
        assertNull(reservation.getSpecialRequests(),
            "Whitespace-only string should convert to null");
    }

    @Test
    @DisplayName("Optional attribute can be explicitly set to null")
    void testOptionalAttributeExplicitNull() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(customer,
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4);

        reservation.setSpecialRequests("Birthday party");
        assertNotNull(reservation.getSpecialRequests());

        // Explicitly set to null
        reservation.setSpecialRequests(null);
        assertNull(reservation.getSpecialRequests());
    }

    @Test
    @DisplayName("Optional attribute with valid value is trimmed")
    void testOptionalAttributeTrimming() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(customer,
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4);

        reservation.setSpecialRequests("  Window seat  ");
        assertEquals("Window seat", reservation.getSpecialRequests(),
            "Optional attribute should be trimmed when set");
    }

    @Test
    @DisplayName("Optional attribute can be changed multiple times")
    void testOptionalAttributeMultipleChanges() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(customer,
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4);

        // Initially null
        assertNull(reservation.getSpecialRequests());

        // Set to first value
        reservation.setSpecialRequests("Birthday");
        assertEquals("Birthday", reservation.getSpecialRequests());

        // Change to second value
        reservation.setSpecialRequests("Anniversary");
        assertEquals("Anniversary", reservation.getSpecialRequests());

        // Clear
        reservation.setSpecialRequests(null);
        assertNull(reservation.getSpecialRequests());
    }

    @Test
    @DisplayName("Optional attribute doesn't affect mandatory validation")
    void testOptionalAttributeIndependent() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        // Reservation should work fine without special requests
        Reservation reservation1 = new Reservation(customer,
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4);
        assertNull(reservation1.getSpecialRequests());

        // Reservation should work fine with special requests
        Reservation reservation2 = new Reservation(customer,
            LocalDate.now().plusDays(8), LocalTime.of(19, 0), 2);
        reservation2.setSpecialRequests("Quiet table");
        assertNotNull(reservation2.getSpecialRequests());

        // Both reservations are valid
        assertEquals(ReservationStatus.PENDING, reservation1.getStatus());
        assertEquals(ReservationStatus.PENDING, reservation2.getStatus());
    }

    @Test
    @DisplayName("Optional attribute with long text")
    void testOptionalAttributeLongText() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(customer,
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4);

        String longRequest = "We are celebrating a special birthday and would appreciate " +
            "a window seat with a view. Please also ensure the table is decorated " +
            "and we would like the staff to sing happy birthday at 8:30 PM.";

        reservation.setSpecialRequests(longRequest);
        assertEquals(longRequest, reservation.getSpecialRequests());
    }

    @Test
    @DisplayName("Optional attribute survives object state changes")
    void testOptionalAttributePersistsThroughStateChanges() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(customer,
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4);

        reservation.setSpecialRequests("Vegetarian menu");
        assertEquals("Vegetarian menu", reservation.getSpecialRequests());

        // Change status
        reservation.confirmReservation();
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());

        // Special request should still be there
        assertEquals("Vegetarian menu", reservation.getSpecialRequests());
    }
}
