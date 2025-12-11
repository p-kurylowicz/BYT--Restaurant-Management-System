import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CompositionAssociationTest {

    @BeforeEach
    void setup() {
        Order.clearExtent();
        Payment.clearExtent();
        Customer.clearExtent();
    }

    @Test
    @DisplayName("Composition Lifecycle: Payment must be created with Order")
    void testPaymentMustBeCreatedWithOrder() {
        Customer customer = new Customer("John", "Doe", "john@test.com", "123456", java.time.LocalDateTime.now());
        Order order = new DineIn(customer);
        Payment payment = new Cash(100.0, order, 100.0);

        // Verify bidirectional connection established during construction
        assertTrue(order.getPayments().contains(payment));
        assertEquals(order, payment.getOrder());
    }

    @Test
    @DisplayName("Composition Lifecycle: Payment constructor requires Order (cannot be null)")
    void testPaymentConstructorRequiresOrder() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Cash(100.0, null, 100.0);
        });
        assertTrue(exception.getMessage().contains("Order cannot be null"));
        assertTrue(exception.getMessage().contains("composition"));
    }

    @Test
    @DisplayName("Composition: Payment cannot be null (mandatory)")
    void testPaymentCannotBeNull() {
        Customer customer = new Customer("Bob", "Jones", "bob@test.com", "789456", java.time.LocalDateTime.now());
        Order order = new DineIn(customer);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            order.addPayment(null);
        });
        assertTrue(exception.getMessage().contains("Payment cannot be null"));
    }

    @Test
    @DisplayName("Composition (1..*): Can create multiple payments for order")
    void testCanAddMultiplePayments() {
        Customer customer = new Customer("Alice", "Brown", "alice@test.com", "456789", java.time.LocalDateTime.now());
        Order order = new DineIn(customer);
        Payment payment1 = new Cash(100.0, order, 100.0);
        Payment payment2 = new Card(50.0, order, "1234", "Visa");

        assertEquals(2, order.getPayments().size());
        assertTrue(order.getPayments().contains(payment1));
        assertTrue(order.getPayments().contains(payment2));
        assertEquals(order, payment1.getOrder());
        assertEquals(order, payment2.getOrder());
    }

    @Test
    @DisplayName("Composition (1..*): Cannot remove last payment")
    void testCannotRemoveLastPayment() {
        Customer customer = new Customer("Charlie", "Davis", "charlie@test.com", "321654", java.time.LocalDateTime.now());
        Order order = new DineIn(customer);
        Payment payment = new Cash(100.0, order, 100.0);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            order.removePayment(payment);
        });
        assertTrue(exception.getMessage().contains("Cannot remove the last payment"));
        assertTrue(exception.getMessage().contains("1..*"));
    }

    @Test
    @DisplayName("Composition Lifecycle: Removing payment deletes it from extent")
    void testRemovePaymentDeletesFromExtent() {
        Customer customer = new Customer("Eve", "Miller", "eve@test.com", "987123", java.time.LocalDateTime.now());
        Order order = new DineIn(customer);
        Payment payment1 = new Cash(100.0, order, 100.0);
        Payment payment2 = new Card(50.0, order, "1234", "Visa");

        assertEquals(2, order.getPayments().size());
        assertEquals(2, Payment.getAllPaymentsFromExtent().size());

        order.removePayment(payment1);

        // Verify payment removed from order
        assertEquals(1, order.getPayments().size());
        assertFalse(order.getPayments().contains(payment1));
        assertTrue(order.getPayments().contains(payment2));

        // Verify payment deleted from extent (composition cascade)
        assertEquals(1, Payment.getAllPaymentsFromExtent().size());
        assertFalse(Payment.getAllPaymentsFromExtent().contains(payment1));
    }

    @Test
    @DisplayName("Composition Lifecycle: Payment cannot be shared between Orders")
    void testPaymentCannotBeSharedBetweenOrders() {
        Customer customer1 = new Customer("Frank", "Wilson", "frank@test.com", "555111", java.time.LocalDateTime.now());
        Customer customer2 = new Customer("Grace", "Moore", "grace@test.com", "555222", java.time.LocalDateTime.now());
        Order order1 = new DineIn(customer1);
        Order order2 = new Takeaway(customer2);
        Payment payment = new Cash(100.0, order1, 100.0);

        // Payment already belongs to order1, trying to associate with order2 should fail
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            payment.setOrder(order2);
        });
        assertTrue(exception.getMessage().contains("already associated with a different Order"));
        assertTrue(exception.getMessage().contains("cannot be shared"));
    }

    @Test
    @DisplayName("Composition Lifecycle: Cascading deletion removes all payments")
    void testCascadingDeletionRemovesAllPayments() {
        Customer customer = new Customer("Henry", "Taylor", "henry@test.com", "555333", java.time.LocalDateTime.now());
        Order order = new DineIn(customer);
        Payment payment1 = new Cash(100.0, order, 100.0);
        Payment payment2 = new Card(50.0, order, "1234", "Visa");
        Payment payment3 = new Card(25.0, order, "5678", "Mastercard");

        // Verify initial state
        assertEquals(3, order.getPayments().size());
        assertEquals(3, Payment.getAllPaymentsFromExtent().size());
        assertEquals(1, Order.getAllOrdersFromExtent().size());

        // Delete the order (should cascade delete all payments)
        order.delete();

        // Verify order removed from extent
        assertEquals(0, Order.getAllOrdersFromExtent().size());

        // Verify all payments removed from extent (composition cascade)
        assertEquals(0, Payment.getAllPaymentsFromExtent().size());

        // Verify order's payment collection is empty
        assertEquals(0, order.getPayments().size());
    }

    @Test
    @DisplayName("Composition Lifecycle: Cascading deletion clears Customer association")
    void testCascadingDeletionClearsCustomerAssociation() {
        Customer customer = new Customer("Ivy", "Anderson", "ivy@test.com", "555444", java.time.LocalDateTime.now());
        Order order = new DineIn(customer);
        Payment payment = new Cash(50.0, order, 50.0);

        // Verify initial connections
        assertEquals(1, customer.getOrders().size());
        assertTrue(customer.getOrders().contains(order));

        // Delete the order
        order.delete();

        // Verify customer-order association is cleared
        assertEquals(0, customer.getOrders().size());
        assertFalse(customer.getOrders().contains(order));
    }

    @Test
    @DisplayName("Composition (1..*): Multiple payments per order")
    void testOneToManyRelationship() {
        Customer customer = new Customer("Kelly", "Jackson", "kelly@test.com", "555666", java.time.LocalDateTime.now());
        Order order = new DineIn(customer);
        Payment payment1 = new Cash(100.0, order, 100.0);
        Payment payment2 = new Card(50.0, order, "1234", "Visa");

        // One order has multiple payments
        assertEquals(2, order.getPayments().size());

        // Each payment belongs to exactly one order
        assertEquals(order, payment1.getOrder());
        assertEquals(order, payment2.getOrder());

        // Verify in extent
        assertEquals(1, Order.getAllOrdersFromExtent().stream()
            .filter(o -> o.getPayments().contains(payment1))
            .count());

        assertEquals(1, Order.getAllOrdersFromExtent().stream()
            .filter(o -> o.getPayments().contains(payment2))
            .count());
    }

    @Test
    @DisplayName("Composition (1..*): Remove non-existent payment throws exception")
    void testRemoveNonExistentPayment() {
        Customer customer = new Customer("Liam", "White", "liam@test.com", "555777", java.time.LocalDateTime.now());
        Order order = new DineIn(customer);
        Payment payment1 = new Cash(100.0, order, 100.0);

        // Create payment for different order
        Customer customer2 = new Customer("Mia", "Harris", "mia@test.com", "555888", java.time.LocalDateTime.now());
        Order order2 = new Takeaway(customer2);
        Payment payment2 = new Card(50.0, order2, "1234", "Visa");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            order.removePayment(payment2);
        });
        assertTrue(exception.getMessage().contains("This payment is not part of this order"));
    }

    @Test
    @DisplayName("Composition (1..*): Null validation for removePayment")
    void testRemovePaymentNullValidation() {
        Customer customer = new Customer("Noah", "Martin", "noah@test.com", "555999", java.time.LocalDateTime.now());
        Order order = new DineIn(customer);
        Payment payment = new Cash(100.0, order, 100.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            order.removePayment(null);
        });
        assertTrue(exception.getMessage().contains("Payment cannot be null"));
    }
}
