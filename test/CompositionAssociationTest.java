import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CompositionAssociationTest {

    @BeforeEach
    void setup() {
        Order.clearExtent();
        Payment.clearExtent();
    }

    @Test
    @DisplayName("Composition: Setting payment establishes reverse connection")
    void testSetPaymentEstablishesReverseConnection() {
        Order order = new DineIn();
        Payment payment = new Cash(100.0, 100.0);

        order.setPayment(payment);

        assertEquals(payment, order.getPayment());
        assertEquals(order, payment.getOrder());
    }

    @Test
    @DisplayName("Composition: Setting order establishes reverse connection")
    void testSetOrderEstablishesReverseConnection() {
        Order order = new DineIn();
        Payment payment = new Cash(100.0, 100.0);

        payment.setOrder(order);

        assertEquals(order, payment.getOrder());
        assertEquals(payment, order.getPayment());
    }

    @Test
    @DisplayName("Composition: Payment cannot be null (mandatory)")
    void testPaymentCannotBeNull() {
        Order order = new DineIn();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            order.setPayment(null);
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
    @DisplayName("Composition: Payment cannot be changed once set")
    void testPaymentCannotBeChanged() {
        Order order = new DineIn();
        Payment payment1 = new Cash(100.0, 100.0);
        Payment payment2 = new Card(100.0, "1234", "Visa");

        order.setPayment(payment1);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            order.setPayment(payment2);
        });
        assertTrue(exception.getMessage().contains("Payment is already set and cannot be changed"));
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

        order.setPayment(payment);

        assertDoesNotThrow(() -> {
            order.setPayment(payment);
        });

        assertEquals(payment, order.getPayment());
        assertEquals(order, payment.getOrder());
    }

    @Test
    @DisplayName("Composition: Bidirectional link established from either side")
    void testBidirectionalLinkFromEitherSide() {
        Order order1 = new DineIn();
        Payment payment1 = new Cash(50.0, 50.0);
        order1.setPayment(payment1);
        assertEquals(order1, payment1.getOrder());
        assertEquals(payment1, order1.getPayment());

        Order order2 = new Takeaway();
        Payment payment2 = new Card(75.0, "5678", "Mastercard");
        payment2.setOrder(order2);
        assertEquals(order2, payment2.getOrder());
        assertEquals(payment2, order2.getPayment());
    }

    @Test
    @DisplayName("Composition: 1:1 relationship enforced")
    void testOneToOneRelationship() {
        Order order = new DineIn();
        Payment payment = new Cash(100.0, 100.0);

        order.setPayment(payment);

        assertEquals(1, Order.getAllOrders().stream()
            .filter(o -> o.getPayment() == payment)
            .count());

        assertEquals(1, Payment.getAllPayments().stream()
            .filter(p -> p.getOrder() == order)
            .count());
    }
}
