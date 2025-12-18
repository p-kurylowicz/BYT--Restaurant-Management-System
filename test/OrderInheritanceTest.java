import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.time.LocalDate;

@DisplayName("Order Inheritance Tests (Disjoint, Complete)")
public class OrderInheritanceTest {

    private Customer customer;

    @BeforeEach
    public void setUp() {
        Order.clearExtent();
        Customer.clearExtent();
        customer = new Customer("John", "Doe", "john@example.com", "+48123456789",
            java.time.LocalDateTime.now());
    }

    @Test
    @DisplayName("Create DineIn order")
    public void testCreateDineInOrder() {
        Order order = Order.createDineIn(customer);

        assertNotNull(order);
        assertTrue(order.isDineIn());
        assertFalse(order.isTakeaway());
        assertEquals(customer, order.getCustomer());
        assertEquals(OrderStatus.ACTIVE, order.getStatus());
        assertNotNull(order.getDineIn().getServingStartTime());
    }

    @Test
    @DisplayName("Create Takeaway order")
    public void testCreateTakeawayOrder() {
        Order order = Order.createTakeaway(customer);

        assertNotNull(order);
        assertTrue(order.isTakeaway());
        assertFalse(order.isDineIn());
        assertEquals(customer, order.getCustomer());
        assertEquals(OrderStatus.ACTIVE, order.getStatus());
        assertFalse(order.getTakeaway().getWasPickedUp());
    }

    @Test
    @DisplayName("DineIn with reservation constructor")
    public void testDineInWithReservation() {
        Table table = new Table(1, 4, "Main");
        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(1),
            LocalTime.of(18, 0),
            4,
            customer,
            table
        );
        Order order = Order.createDineIn(customer, reservation);

        assertNotNull(order);
        assertTrue(order.isDineIn());
        assertEquals(reservation, order.getDineIn().getReservation());
    }

    @Test
    @DisplayName("Takeaway with collection time constructor")
    public void testTakeawayWithCollectionTime() {
        LocalTime collectionTime = LocalTime.of(18, 30);
        Order order = Order.createTakeaway(customer, collectionTime);

        assertNotNull(order);
        assertTrue(order.isTakeaway());
        assertEquals(collectionTime, order.getTakeaway().getCollectionTime());
        assertFalse(order.getTakeaway().getWasPickedUp());
    }

    @Test
    @DisplayName("Both order types added to shared extent")
    public void testSharedExtent() {
        Order dineIn = Order.createDineIn(customer);
        Order takeaway = Order.createTakeaway(customer);

        assertEquals(2, Order.getAllOrdersFromExtent().size());
        assertTrue(Order.getAllOrdersFromExtent().contains(dineIn));
        assertTrue(Order.getAllOrdersFromExtent().contains(takeaway));
    }

    @Test
    @DisplayName("Polymorphic reference to Order works")
    public void testPolymorphicReference() {
        Order order1 = Order.createDineIn(customer);
        Order order2 = Order.createTakeaway(customer);

        assertEquals(OrderStatus.ACTIVE, order1.getStatus());
        assertEquals(OrderStatus.ACTIVE, order2.getStatus());
        assertEquals(customer, order1.getCustomer());
        assertEquals(customer, order2.getCustomer());
    }

    @Test
    @DisplayName("DineIn-specific: Add tables")
    public void testDineInTableManagement() {
        Order order = Order.createDineIn(customer);
        Table table1 = new Table(1, 4, "Main");
        Table table2 = new Table(2, 2, "Main");

        order.getDineIn().addTable(table1);
        order.getDineIn().addTable(table2);

        assertEquals(2, order.getDineIn().getTables().size());
        assertTrue(order.getDineIn().getTables().contains(table1));
        assertTrue(order.getDineIn().getTables().contains(table2));
        assertEquals(TableStatus.OCCUPIED, table1.getStatus());
    }

    @Test
    @DisplayName("DineIn-specific: Release tables on completion")
    public void testDineInReleaseTables() {
        Order order = Order.createDineIn(customer);
        Table table = new Table(1, 4, "Main");
        order.getDineIn().addTable(table);

        assertEquals(TableStatus.OCCUPIED, table.getStatus());

        order.finalizeOrder();
        order.completeOrder();

        assertEquals(TableStatus.AVAILABLE, table.getStatus());
    }

    @Test
    @DisplayName("DineIn-specific: Cannot add null table")
    public void testDineInNullTableValidation() {
        Order order = Order.createDineIn(customer);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            order.getDineIn().addTable(null);
        });

        assertTrue(exception.getMessage().contains("Table cannot be null"));
    }

    @Test
    @DisplayName("Takeaway-specific: Set collection time")
    public void testTakeawayCollectionTime() {
        Order order = Order.createTakeaway(customer);
        LocalTime collectionTime = LocalTime.of(19, 0);

        order.getTakeaway().setCollectionTime(collectionTime);

        assertEquals(collectionTime, order.getTakeaway().getCollectionTime());
    }

    @Test
    @DisplayName("Takeaway-specific: Mark as picked up")
    public void testTakeawayMarkAsPickedUp() {
        Order order = Order.createTakeaway(customer);

        order.finalizeOrder();
        order.completeOrder();
        order.markAsPickedUp();

        assertTrue(order.getTakeaway().getWasPickedUp());
    }

    @Test
    @DisplayName("Takeaway-specific: Cannot mark as picked up if not completed")
    public void testTakeawayPickupValidation() {
        Order order = Order.createTakeaway(customer);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            order.markAsPickedUp();
        });

        assertTrue(exception.getMessage().contains("Order must be completed"));
    }

    @Test
    @DisplayName("Takeaway-specific: Cannot set null collection time")
    public void testTakeawayNullCollectionTimeValidation() {
        Order order = Order.createTakeaway(customer);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            order.getTakeaway().setCollectionTime(null);
        });

        assertTrue(exception.getMessage().contains("Collection time cannot be null"));
    }

    @Test
    @DisplayName("Common behavior: Finalize and complete order workflow")
    public void testOrderWorkflow() {
        Order dineIn = Order.createDineIn(customer);
        Order takeaway = Order.createTakeaway(customer);

        assertEquals(OrderStatus.ACTIVE, dineIn.getStatus());
        assertEquals(OrderStatus.ACTIVE, takeaway.getStatus());

        dineIn.finalizeOrder();
        takeaway.finalizeOrder();

        assertEquals(OrderStatus.AWAITING_PAYMENT, dineIn.getStatus());
        assertEquals(OrderStatus.AWAITING_PAYMENT, takeaway.getStatus());

        dineIn.completeOrder();
        takeaway.completeOrder();

        assertEquals(OrderStatus.COMPLETED, dineIn.getStatus());
        assertEquals(OrderStatus.COMPLETED, takeaway.getStatus());
    }

    @Test
    @DisplayName("Common behavior: Cancel order")
    public void testCancelOrder() {
        Order order = Order.createDineIn(customer);

        order.cancelOrder();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    @DisplayName("Common behavior: Cannot cancel completed order")
    public void testCannotCancelCompletedOrder() {
        Order order = Order.createTakeaway(customer);
        order.finalizeOrder();
        order.completeOrder();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            order.cancelOrder();
        });

        assertTrue(exception.getMessage().contains("Cannot cancel order"));
    }

    @Test
    @DisplayName("Common behavior: Payment management")
    public void testPaymentManagement() {
        Order order = Order.createDineIn(customer);
        Payment payment = new Card(100.0, order, "1234", "VISA");

        assertTrue(order.getPayments().contains(payment));
        assertEquals(1, order.getPayments().size());
    }

    @Test
    @DisplayName("Common behavior: Discount application")
    public void testDiscountApplication() {
        Order order = Order.createTakeaway(customer);
        Discount discount = new OrderLevelDiscount("TEST10",
            java.time.LocalDate.now(), java.time.LocalTime.now(), 10.0);

        order.setDiscount(discount);

        assertTrue(order.hasDiscount());
        assertEquals(discount, order.getDiscount());
    }

    @Test
    @DisplayName("Complete constraint: Every order must be DineIn or Takeaway")
    public void testCompleteConstraint() {
        // Every order must be either DineIn or Takeaway (complete constraint)
        // This is enforced by the factory methods

        Order dineIn = Order.createDineIn(customer);
        Order takeaway = Order.createTakeaway(customer);

        // Each order must be exactly one type
        assertTrue(dineIn.isDineIn() || dineIn.isTakeaway());
        assertTrue(takeaway.isDineIn() || takeaway.isTakeaway());

        // Verify they are the correct types
        assertTrue(dineIn.isDineIn());
        assertTrue(takeaway.isTakeaway());
    }

    @Test
    @DisplayName("Disjoint constraint: Order cannot be both DineIn and Takeaway")
    public void testDisjointConstraint() {
        Order dineIn = Order.createDineIn(customer);
        Order takeaway = Order.createTakeaway(customer);

        // Each order is exactly one type (disjoint constraint)
        assertTrue(dineIn.isDineIn() && !dineIn.isTakeaway());
        assertTrue(takeaway.isTakeaway() && !takeaway.isDineIn());

        // Verify XOR constraint: exactly one must be non-null
        assertFalse(dineIn.isDineIn() && dineIn.isTakeaway());
        assertFalse(takeaway.isDineIn() && takeaway.isTakeaway());
    }

    @Test
    @DisplayName("Type checking with isDineIn/isTakeaway")
    public void testInstanceofChecking() {
        Order order1 = Order.createDineIn(customer);
        Order order2 = Order.createTakeaway(customer);

        if (order1.isDineIn()) {
            DineIn dineIn = order1.getDineIn();
            assertNotNull(dineIn.getServingStartTime());
        }

        if (order2.isTakeaway()) {
            Takeaway takeaway = order2.getTakeaway();
            assertFalse(takeaway.getWasPickedUp());
        }
    }

    @Test
    @DisplayName("DineIn toString includes type-specific info")
    public void testDineInToString() {
        Order order = Order.createDineIn(customer);
        String str = order.toString();

        assertNotNull(str);
        assertTrue(str.contains("DineIn") || str.contains("DINE_IN"));
        assertTrue(str.contains("servingStartTime"));
    }

    @Test
    @DisplayName("Takeaway toString includes type-specific info")
    public void testTakeawayToString() {
        Order order = Order.createTakeaway(customer);
        String str = order.toString();

        assertNotNull(str);
        assertTrue(str.contains("Takeaway") || str.contains("TAKEAWAY"));
        assertTrue(str.contains("wasPickedUp"));
    }

    @Test
    @DisplayName("Order deletion removes from extent")
    public void testOrderDeletion() {
        Order order = Order.createDineIn(customer);
        assertEquals(1, Order.getAllOrdersFromExtent().size());

        order.delete();

        assertEquals(0, Order.getAllOrdersFromExtent().size());
    }

    @Test
    @DisplayName("Cannot create order with null customer")
    public void testNullCustomerValidation() {
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            Order.createDineIn(null);
        });

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            Order.createTakeaway(null);
        });

        assertTrue(exception1.getMessage().contains("Customer cannot be null"));
        assertTrue(exception2.getMessage().contains("Customer cannot be null"));
    }

    @Test
    @DisplayName("DineIn reservation can be null (optional)")
    public void testOptionalReservation() {
        Order order = Order.createDineIn(customer);

        assertNull(order.getDineIn().getReservation());
    }

    @Test
    @DisplayName("Takeaway collection time can be null (optional)")
    public void testOptionalCollectionTime() {
        Order order = Order.createTakeaway(customer);

        assertNull(order.getTakeaway().getCollectionTime());
    }
}
