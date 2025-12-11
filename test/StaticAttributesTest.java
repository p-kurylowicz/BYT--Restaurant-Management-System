import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Tests for static attribute implementation.
 *
 * <p>Test categories:
 * <ul>
 *   <li>Constant values: TAX_RATE (0.23), CANCELLATION_WINDOW_HOURS (4)</li>
 *   <li>Uniform application: same value across all instances</li>
 *   <li>Inheritance: static attributes accessible in subclasses</li>
 *   <li>Business rules: used in calculations and validations</li>
 * </ul>
 */
public class StaticAttributesTest {

    @BeforeEach
    void clearExtents() {
        Customer.clearExtent();
        Ingredient.clearExtent();
        MenuItem.clearExtent();
        Reservation.clearExtent();
    }

    @Test
    @DisplayName("MenuItem.TAX_RATE is 23% (0.23)")
    void testTaxRateValue() {
        assertEquals(0.23, MenuItem.TAX_RATE, 0.001);
    }

    @Test
    @DisplayName("TAX_RATE applies to all MenuItem subclasses")
    void testTaxRateInheritance() {
        NutritionalInfo nutrition = new NutritionalInfo(450, 20.0, 45.0, 15.0, 4.0);

        // Create different MenuItem subclasses
        MainDish mainDish = new MainDish("Spaghetti", "Pasta dish", 40.0, "/img.jpg",
            "Italian", nutrition,  1);
        Beverage beverage = new Beverage("Wine", "Red wine", 25.0, "/img.jpg",
            "French", nutrition,  12.5);
        Dessert dessert = new Dessert("Tiramisu", "Italian dessert", 18.0, "/img.jpg",
            "Italian", nutrition,  false);

        // All use the same TAX_RATE
        double expectedMainDishPrice = 40.0 * (1 + MenuItem.TAX_RATE);
        double expectedBeveragePrice = 25.0 * (1 + MenuItem.TAX_RATE);
        double expectedDessertPrice = 18.0 * (1 + MenuItem.TAX_RATE);

        assertEquals(expectedMainDishPrice, mainDish.calculatePriceWithTax(), 0.01);
        assertEquals(expectedBeveragePrice, beverage.calculatePriceWithTax(), 0.01);
        assertEquals(expectedDessertPrice, dessert.calculatePriceWithTax(), 0.01);
    }

    @Test
    @DisplayName("Calculate price with tax using TAX_RATE")
    void testCalculatePriceWithTax() {
        NutritionalInfo nutrition = new NutritionalInfo(450, 20.0, 45.0, 15.0, 4.0);

        MainDish dish = new MainDish("Spaghetti", "Pasta dish", 40.0, "/img.jpg",
            "Italian", nutrition,  1);

        double basePrice = 40.0;
        double expectedPrice = basePrice * (1 + MenuItem.TAX_RATE); // 40.0 * 1.23 = 49.20
        assertEquals(expectedPrice, dish.calculatePriceWithTax(), 0.01);
        assertEquals(49.20, dish.calculatePriceWithTax(), 0.01);
    }

    @Test
    @DisplayName("Reservation.CANCELLATION_WINDOW_HOURS is 4 hours")
    void testCancellationWindowValue() {
        assertEquals(4, Reservation.CANCELLATION_WINDOW_HOURS);
    }

    @Test
    @DisplayName("Reservation far in future can be cancelled")
    void testReservationCanBeCancelled() {
        Customer customer = new Customer("Jane", "Smith", "jane@example.com",
            "+48987654321", LocalDateTime.now().minusMonths(6));
        Table table = new Table(1, 4, "Section A");

        // Reservation 7 days in future - can be cancelled
        Reservation futureReservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(19, 0), 4, customer, null);

        assertTrue(futureReservation.canBeCancelled());
    }

    @Test
    @DisplayName("Reservation within cancellation window cannot be cancelled")
    void testReservationCannotBeCancelled() {
        Customer customer = new Customer("Jane", "Smith", "jane@example.com",
            "+48987654321", LocalDateTime.now().minusMonths(6));
        Table table = new Table(2, 2, "Section A");

        // Reservation 2 hours from now - within 4-hour window
        Reservation soonReservation = new Reservation(
            LocalDate.now(), LocalTime.now().plusHours(2), 2, customer, null);

        assertFalse(soonReservation.canBeCancelled());
    }

    @Test
    @DisplayName("Cancelling reservation within window throws exception")
    void testCancelReservationWithinWindow() {
        Customer customer = new Customer("Jane", "Smith", "jane@example.com",
            "+48987654321", LocalDateTime.now().minusMonths(6));
        Table table = new Table(3, 2, "Section A");

        Reservation reservation = new Reservation(
            LocalDate.now(), LocalTime.now().plusHours(2), 2, customer, null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reservation.cancelReservation();
        });

        assertTrue(exception.getMessage().contains("4 hours"));
    }

    @Test
    @DisplayName("Cancelling reservation outside window succeeds")
    void testCancelReservationOutsideWindow() {
        Customer customer = new Customer("Jane", "Smith", "jane@example.com",
            "+48987654321", LocalDateTime.now().minusMonths(6));
        Table table = new Table(4, 4, "Section A");

        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(7), LocalTime.of(19, 0), 4, customer, null);

        assertTrue(reservation.canBeCancelled());
        reservation.confirmReservation();

        // Should not throw exception
        assertDoesNotThrow(() -> reservation.cancelReservation());
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
    }

    @Test
    @DisplayName("TAX_RATE applies uniformly to different price points")
    void testTaxRateUniformApplication() {
        NutritionalInfo nutrition = new NutritionalInfo(300, 15, 30, 10, 3);

        MainDish cheapDish = new MainDish("Soup", "Simple soup", 10.0, "/img.jpg",
            "Polish", nutrition,  0);
        MainDish expensiveDish = new MainDish("Steak", "Premium steak", 100.0, "/img.jpg",
            "American", nutrition,  1);

        assertEquals(10.0 * 1.23, cheapDish.calculatePriceWithTax(), 0.01);
        assertEquals(100.0 * 1.23, expensiveDish.calculatePriceWithTax(), 0.01);
    }

    @Test
    @DisplayName("Reservation just beyond 4 hours boundary is cancellable")
    void testReservationBeyondBoundary() {
        Customer customer = new Customer("Jane", "Smith", "jane@example.com",
            "+48987654321", LocalDateTime.now().minusMonths(6));
        Table table = new Table(5, 2, "Section A");

        // Reservation 5 hours from now - use LocalDateTime to handle day transitions correctly
        LocalDateTime futureTime = LocalDateTime.now().plusHours(5);
        Reservation boundaryReservation = new Reservation(
            futureTime.toLocalDate(), futureTime.toLocalTime(), 2, customer, null);

        assertTrue(boundaryReservation.canBeCancelled(),
            "Reservation at 5 hours should be cancellable (beyond 4-hour window)");
    }
}
