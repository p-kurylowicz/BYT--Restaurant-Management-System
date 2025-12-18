import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class BasicAssociationTest {

    @BeforeEach
    void setup() {
        Customer.clearExtent();
        Order.clearExtent();
    }

    // ============================================
    // ADDITION TESTS (Establishing Connections)
    // ============================================

    @Test
    @DisplayName("Basic Association: Add order to customer (Reverse Connection)")
    void testAddOrderToCustomer() {
        Customer customer = new Customer("John", "Doe", "john@test.com", "123456", LocalDateTime.now());
        Customer tempCustomer = new Customer("Temp", "Customer", "temp@test.com", "000000", LocalDateTime.now());
        Order order = Order.createDineIn(tempCustomer);

        customer.addOrder(order);

        assertTrue(customer.getOrders().contains(order));
        assertEquals(customer, order.getCustomer());
    }

    @Test
    @DisplayName("Basic Association: Set customer for order (Reverse Connection)")
    void testSetCustomerForOrder() {
        Customer customer = new Customer("Jane", "Doe", "jane@test.com", "654321", LocalDateTime.now());
        Customer tempCustomer = new Customer("Temp", "Customer", "temp@test.com", "000000", LocalDateTime.now());
        Order order = Order.createTakeaway(tempCustomer);

        order.setCustomer(customer);

        assertEquals(customer, order.getCustomer());
        assertTrue(customer.getOrders().contains(order));
    }

    @Test
    @DisplayName("Basic Association: Change customer for an order")
    void testChangeCustomer() {
        Customer c1 = new Customer("C1", "D1", "c1@test.com", "111", LocalDateTime.now());
        Customer c2 = new Customer("C2", "D2", "c2@test.com", "222", LocalDateTime.now());
        Order order = Order.createDineIn(c1);

        assertEquals(c1, order.getCustomer());

        order.setCustomer(c2);

        assertEquals(c2, order.getCustomer());
        assertFalse(c1.getOrders().contains(order));
        assertTrue(c2.getOrders().contains(order));
    }

    // ============================================
    // REMOVAL TESTS
    // ============================================

    @Test
    @DisplayName("Basic Association: Remove order from customer")
    void testRemoveOrderFromCustomer() {
        Customer customer = new Customer("John", "Doe", "john@test.com", "123456", LocalDateTime.now());
        Order order = Order.createDineIn(customer);

        customer.removeOrder(order);

        assertFalse(customer.getOrders().contains(order));
        assertNull(order.getCustomer());
    }

    // ============================================
    // EXCEPTION/ERROR HANDLING TESTS
    // ============================================

    @Test
    @DisplayName("Basic Association: Error Handling (Null)")
    void testNullErrorHandling() {
        Customer customer = new Customer("John", "Doe", "john@test.com", "123456", LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> customer.addOrder(null));
        assertThrows(IllegalArgumentException.class, () -> customer.removeOrder(null));
    }
}
