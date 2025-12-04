import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CompositionAssociationTest {

    @BeforeEach
    void setup() {
        Order.clearExtent();
        Payment.clearExtent();
    }

    @Test
    @DisplayName("Composition: Adding payment establishes reverse connection")
    void testAddPaymentEstablishesReverseConnection() {
        Order order = new DineIn();
        Payment payment = new Cash(100.0, 100.0);

        order.addPayment(payment);

        assertTrue(order.getPayments().contains(payment));
        assertEquals(order, payment.getOrder());
    }

    @Test
    @DisplayName("Composition: Setting order establishes reverse connection")
    void testSetOrderEstablishesReverseConnection() {
        Order order = new DineIn();
        Payment payment = new Cash(100.0, 100.0);

        payment.setOrder(order);

        assertEquals(order, payment.getOrder());
        assertTrue(order.getPayments().contains(payment));
    }

    @Test
    @DisplayName("Composition: Payment cannot be null (mandatory)")
    void testPaymentCannotBeNull() {
        Order order = new DineIn();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            order.addPayment(null);
        });
        assertTrue(exception.getMessage().contains("Payment cannot be null"));
    }

    @Test
    @DisplayName("Composition: Order cannot be null (mandatory)")
    void testOrderCannotBeNull() {
        Payment payment = new Cash(100.0, 100.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            payment.setOrder(null);
        });
        assertTrue(exception.getMessage().contains("Order cannot be null"));
    }

    @Test
    @DisplayName("Composition (1..*): Can add multiple payments to order")
    void testCanAddMultiplePayments() {
        Order order = new DineIn();
        Payment payment1 = new Cash(100.0, 100.0);
        Payment payment2 = new Card(50.0, "1234", "Visa");

        order.addPayment(payment1);
        order.addPayment(payment2);

        assertEquals(2, order.getPayments().size());
        assertTrue(order.getPayments().contains(payment1));
        assertTrue(order.getPayments().contains(payment2));
        assertEquals(order, payment1.getOrder());
        assertEquals(order, payment2.getOrder());
    }

    @Test
    @DisplayName("Composition (1..*): Cannot remove last payment")
    void testCannotRemoveLastPayment() {
        Order order = new DineIn();
        Payment payment = new Cash(100.0, 100.0);

        order.addPayment(payment);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            order.removePayment(payment);
        });
        assertTrue(exception.getMessage().contains("Cannot remove the last payment"));
        assertTrue(exception.getMessage().contains("1..*"));
    }

    @Test
    @DisplayName("Composition (1..*): Can remove payment when multiple exist")
    void testCanRemovePaymentWhenMultipleExist() {
        Order order = new DineIn();
        Payment payment1 = new Cash(100.0, 100.0);
        Payment payment2 = new Card(50.0, "1234", "Visa");

        order.addPayment(payment1);
        order.addPayment(payment2);

        assertEquals(2, order.getPayments().size());

        assertDoesNotThrow(() -> {
            order.removePayment(payment1);
        });

        assertEquals(1, order.getPayments().size());
        assertFalse(order.getPayments().contains(payment1));
        assertTrue(order.getPayments().contains(payment2));
    }

    @Test
    @DisplayName("Composition: Order cannot be changed once set")
    void testOrderCannotBeChanged() {
        Order order1 = new DineIn();
        Order order2 = new Takeaway();
        Payment payment = new Cash(100.0, 100.0);

        payment.setOrder(order1);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            payment.setOrder(order2);
        });
        assertTrue(exception.getMessage().contains("Order is already set and cannot be changed"));
    }

    @Test
    @DisplayName("Composition: Duplicate assignment is handled gracefully")
    void testDuplicateAssignmentHandled() {
        Order order = new DineIn();
        Payment payment = new Cash(100.0, 100.0);

        order.addPayment(payment);

        assertDoesNotThrow(() -> {
            order.addPayment(payment);
        });

        assertEquals(1, order.getPayments().size());
        assertTrue(order.getPayments().contains(payment));
        assertEquals(order, payment.getOrder());
    }

    @Test
    @DisplayName("Composition: Bidirectional link established from either side")
    void testBidirectionalLinkFromEitherSide() {
        Order order1 = new DineIn();
        Payment payment1 = new Cash(50.0, 50.0);
        order1.addPayment(payment1);
        assertEquals(order1, payment1.getOrder());
        assertTrue(order1.getPayments().contains(payment1));

        Order order2 = new Takeaway();
        Payment payment2 = new Card(75.0, "5678", "Mastercard");
        payment2.setOrder(order2);
        assertEquals(order2, payment2.getOrder());
        assertTrue(order2.getPayments().contains(payment2));
    }

    @Test
    @DisplayName("Composition (1..*): Multiple payments per order")
    void testOneToManyRelationship() {
        Order order = new DineIn();
        Payment payment1 = new Cash(100.0, 100.0);
        Payment payment2 = new Card(50.0, "1234", "Visa");

        order.addPayment(payment1);
        order.addPayment(payment2);

        // One order has multiple payments
        assertEquals(2, order.getPayments().size());

        // Each payment belongs to exactly one order
        assertEquals(order, payment1.getOrder());
        assertEquals(order, payment2.getOrder());

        // Verify in extent
        assertEquals(1, Order.getAllOrders().stream()
            .filter(o -> o.getPayments().contains(payment1))
            .count());

        assertEquals(1, Order.getAllOrders().stream()
            .filter(o -> o.getPayments().contains(payment2))
            .count());
    }

    @Test
    @DisplayName("Composition (1..*): Remove non-existent payment throws exception")
    void testRemoveNonExistentPayment() {
        Order order = new DineIn();
        Payment payment1 = new Cash(100.0, 100.0);
        Payment payment2 = new Card(50.0, "1234", "Visa");

        order.addPayment(payment1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            order.removePayment(payment2);
        });
        assertTrue(exception.getMessage().contains("This payment is not part of this order"));
    }

    @Test
    @DisplayName("Composition (1..*): Null validation for removePayment")
    void testRemovePaymentNullValidation() {
        Order order = new DineIn();
        Payment payment = new Cash(100.0, 100.0);
        order.addPayment(payment);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            order.removePayment(null);
        });
        assertTrue(exception.getMessage().contains("Payment cannot be null"));
    }
}
