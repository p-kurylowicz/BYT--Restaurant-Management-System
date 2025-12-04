import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Tests for optional multi-value attribute implementation.
 *
 * <p>Test categories:
 * <ul>
 *   <li>Empty defaults: optional multi-value attributes can be empty initially</li>
 *   <li>Adding values: optional multi-value attributes can have values added</li>
 *   <li>Validation: values still meet normal constraints</li>
 * </ul>
 */
public class OptionalAttributesTest {

    @BeforeEach
    void clearExtents() {
        Customer.clearExtent();
        Reservation.clearExtent();
    }

    @Test
    @DisplayName("Optional multi-value attribute is empty by default")
    void testOptionalAttributeDefaultEmpty() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(18, 30), 2, customer);

        assertTrue(reservation.getSpecialRequests().isEmpty(),
            "Optional multi-value attribute should be empty by default");
    }

    @Test
    @DisplayName("Optional multi-value attribute can have values added")
    void testOptionalAttributeWithValue() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4, customer);
        reservation.addSpecialRequest("Window seat");
        reservation.addSpecialRequest("Birthday celebration");

        assertFalse(reservation.getSpecialRequests().isEmpty());
        assertEquals(2, reservation.getSpecialRequests().size());
        assertTrue(reservation.getSpecialRequests().contains("Window seat"));
        assertTrue(reservation.getSpecialRequests().contains("Birthday celebration"));
    }

    @Test
    @DisplayName("Optional multi-value attribute rejects empty string")
    void testOptionalAttributeEmptyStringRejected() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4, customer);

        reservation.addSpecialRequest("Some request");
        assertEquals(1, reservation.getSpecialRequests().size());

        // Attempt to add empty string
        assertThrows(IllegalArgumentException.class, () -> {
            reservation.addSpecialRequest("");
        }, "Empty string should throw exception");
    }

    @Test
    @DisplayName("Optional multi-value attribute rejects whitespace-only string")
    void testOptionalAttributeWhitespaceRejected() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4, customer);

        assertThrows(IllegalArgumentException.class, () -> {
            reservation.addSpecialRequest("   ");
        }, "Whitespace-only string should throw exception");
    }

    @Test
    @DisplayName("Optional multi-value attribute can be cleared")
    void testOptionalAttributeCleared() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4, customer);

        reservation.addSpecialRequest("Birthday party");
        assertFalse(reservation.getSpecialRequests().isEmpty());

        // Clear all requests
        reservation.clearSpecialRequests();
        assertTrue(reservation.getSpecialRequests().isEmpty());
    }

    @Test
    @DisplayName("Optional multi-value attribute values are trimmed")
    void testOptionalAttributeTrimming() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4, customer);

        reservation.addSpecialRequest("  Window seat  ");
        assertTrue(reservation.getSpecialRequests().contains("Window seat"),
            "Optional attribute should be trimmed when added");
    }

    @Test
    @DisplayName("Optional multi-value attribute can be changed multiple times")
    void testOptionalAttributeMultipleChanges() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4, customer);

        // Initially empty
        assertTrue(reservation.getSpecialRequests().isEmpty());

        // Add first value
        reservation.addSpecialRequest("Birthday");
        assertEquals(1, reservation.getSpecialRequests().size());
        assertTrue(reservation.getSpecialRequests().contains("Birthday"));

        // Add second value
        reservation.addSpecialRequest("Anniversary");
        assertEquals(2, reservation.getSpecialRequests().size());

        // Remove first value
        reservation.removeSpecialRequest("Birthday");
        assertEquals(1, reservation.getSpecialRequests().size());
        assertFalse(reservation.getSpecialRequests().contains("Birthday"));

        // Clear all
        reservation.clearSpecialRequests();
        assertTrue(reservation.getSpecialRequests().isEmpty());
    }

    @Test
    @DisplayName("Optional multi-value attribute doesn't affect mandatory validation")
    void testOptionalAttributeIndependent() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        // Reservation should work fine without special requests
        Reservation reservation1 = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4, customer);
        assertTrue(reservation1.getSpecialRequests().isEmpty());

        // Reservation should work fine with special requests
        Reservation reservation2 = new Reservation(
            LocalDate.now().plusDays(8), LocalTime.of(19, 0), 2, customer);
        reservation2.addSpecialRequest("Quiet table");
        assertFalse(reservation2.getSpecialRequests().isEmpty());

        // Both reservations are valid
        assertEquals(ReservationStatus.PENDING, reservation1.getStatus());
        assertEquals(ReservationStatus.PENDING, reservation2.getStatus());
    }

    @Test
    @DisplayName("Optional multi-value attribute with long text")
    void testOptionalAttributeLongText() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4, customer);

        String longRequest = "We are celebrating a special birthday and would appreciate " +
            "a window seat with a view. Please also ensure the table is decorated " +
            "and we would like the staff to sing happy birthday at 8:30 PM.";

        reservation.addSpecialRequest(longRequest);
        assertTrue(reservation.getSpecialRequests().contains(longRequest));
    }

    @Test
    @DisplayName("Optional multi-value attribute survives object state changes")
    void testOptionalAttributePersistsThroughStateChanges() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4, customer);

        reservation.addSpecialRequest("Vegetarian menu");
        assertTrue(reservation.getSpecialRequests().contains("Vegetarian menu"));

        // Change status
        reservation.confirmReservation();
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());

        // Special request should still be there
        assertTrue(reservation.getSpecialRequests().contains("Vegetarian menu"));
    }

    @Test
    @DisplayName("Optional multi-value attribute rejects null values")
    void testOptionalAttributeRejectsNull() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4, customer);

        assertThrows(IllegalArgumentException.class, () -> {
            reservation.addSpecialRequest(null);
        }, "Null value should throw exception");
    }

    @Test
    @DisplayName("Optional multi-value attribute prevents duplicate values")
    void testOptionalAttributePreventsDuplicates() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4, customer);

        reservation.addSpecialRequest("Window seat");
        reservation.addSpecialRequest("Window seat");  // Duplicate

        assertEquals(1, reservation.getSpecialRequests().size(),
            "Duplicate values should not be added");
    }

    @Test
    @DisplayName("Optional multi-value attribute returns unmodifiable list")
    void testOptionalAttributeUnmodifiable() {
        Customer customer = new Customer("Alice", "Brown", "alice@example.com",
            "+48123123123", LocalDateTime.now().minusYears(1));

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(20, 0), 4, customer);

        reservation.addSpecialRequest("Window seat");

        assertThrows(UnsupportedOperationException.class, () -> {
            reservation.getSpecialRequests().add("Hacker attempt");
        }, "Direct modification of returned list should throw exception");
    }
}
