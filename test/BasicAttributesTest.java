import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tests for basic attribute validation and constraints.
 *
 * <p>Test categories:
 * <ul>
 *   <li>String validation: non-null, non-empty, trimming</li>
 *   <li>Numeric validation: non-negative values, range constraints</li>
 *   <li>Date validation: past dates, future dates</li>
 *   <li>Email validation: format checks</li>
 * </ul>
 */
public class BasicAttributesTest {

    @BeforeEach
    void clearExtents() {
        Customer.clearExtent();
        Ingredient.clearExtent();
    }

    @Test
    @DisplayName("Valid customer creation with all basic attributes")
    void testValidCustomerCreation() {
        Customer customer = new Customer("John", "Doe", "john@example.com",
            "+48123456789", LocalDateTime.now().minusYears(1));

        assertEquals("John", customer.getName());
        assertEquals("Doe", customer.getSurname());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("+48123456789", customer.getPhone());
        assertNotNull(customer.getRegistrationDate());
    }

    @Test
    @DisplayName("Empty string should throw IllegalArgumentException")
    void testEmptyStringValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer("", "Doe", "john@example.com", "+48123456789", LocalDateTime.now());
        }, "Empty name should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Null string should throw IllegalArgumentException")
    void testNullStringValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer(null, "Doe", "john@example.com", "+48123456789", LocalDateTime.now());
        }, "Null name should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Future date validation for registration date")
    void testFutureDateValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer("John", "Doe", "john@example.com",
                "+48123456789", LocalDateTime.now().plusDays(1));
        }, "Future registration date should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Negative number validation for stock")
    void testNegativeNumberValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Ingredient("Tomato", "kg", -5, 10, 2.5);
        }, "Negative stock should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Negative price validation")
    void testNegativePriceValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Ingredient("Tomato", "kg", 10, 5, -2.5);
        }, "Negative price should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Email validation - must contain @")
    void testEmailValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer("John", "Doe", "invalid-email", "+48123456789", LocalDateTime.now());
        }, "Email without @ should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Whitespace-only strings should be rejected")
    void testWhitespaceStringValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer("   ", "Doe", "john@example.com", "+48123456789", LocalDateTime.now());
        }, "Whitespace-only name should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Zero price should throw IllegalArgumentException")
    void testZeroPriceValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Ingredient("Tomato", "kg", 10, 5, 0);
        }, "Zero price should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Future hire date should be rejected")
    void testFutureHireDateValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Waiter("Tom", "tom@restaurant.com", "+48111222333",
                LocalDate.now().plusDays(1), 25.0, "Section A");
        }, "Future hire date should throw IllegalArgumentException");
    }
}
