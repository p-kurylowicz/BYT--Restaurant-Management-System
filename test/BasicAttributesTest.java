import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BasicAttributesTest {

    @BeforeEach
    void clearExtents() {
        Customer.clearExtent();
        Ingredient.clearExtent();
        Employee.clearExtent();
    }

    @Test
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
    void testEmptyStringValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer("", "Doe", "john@example.com", "+48123456789", LocalDateTime.now());
        });
    }

    @Test
    void testNullStringValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer(null, "Doe", "john@example.com", "+48123456789", LocalDateTime.now());
        });
    }

    @Test
    void testFutureDateValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer("John", "Doe", "john@example.com",
                "+48123456789", LocalDateTime.now().plusDays(1));
        });
    }

    @Test
    void testNegativeNumberValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Ingredient("Tomato", "kg", -5, 10, 2.5);
        });
    }

    @Test
    void testNegativePriceValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Ingredient("Tomato", "kg", 10, 5, -2.5);
        });
    }

    @Test
    void testEmailValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer("John", "Doe", "invalid-email", "+48123456789", LocalDateTime.now());
        });
    }

    @Test
    void testWhitespaceStringValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer("   ", "Doe", "john@example.com", "+48123456789", LocalDateTime.now());
        });
    }

    @Test
    void testZeroPriceValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Ingredient("Tomato", "kg", 10, 5, 0);
        });
    }

    @Test
    void testFutureHireDateValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Employee("Tom", "+48111222333", "tom@restaurant.com", "123 Test St",
                LocalDate.now().plusDays(1), 25.0, "Section A");
        });
    }
}
