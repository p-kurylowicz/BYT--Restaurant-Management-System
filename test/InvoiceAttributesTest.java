import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class InvoiceAttributesTest {

    private Order completedOrder;
    private Order activeOrder;

    @BeforeEach
    void setup() {
        Invoice.clearExtent();
        Order.clearExtent();
        Payment.clearExtent();

        Customer customer1 = new Customer();
        completedOrder = Order.createDineIn(customer1);
        completedOrder.finalizeOrder();
        completedOrder.completeOrder();

        Customer customer2 = new Customer();
        activeOrder = Order.createDineIn(customer2);
    }

    
    @Test
    @DisplayName("Method: checkAndMarkPaid() correctly updates status")
    void testCheckAndMarkPaid() {
        Address validAddress = new Address("St. Test", "City", "00-001", "PL");
        Invoice invoice = new Invoice(completedOrder, validAddress, 100.0, null);
        assertFalse(invoice.isPaid(), "Invoice should start as unpaid");

        invoice.addPayment(new Cash(50.0, completedOrder, 50.0));
        assertFalse(invoice.isPaid(), "Partial payment should not mark as paid");

        invoice.addPayment(new Cash(50.0, completedOrder, 50.0));
        assertTrue(invoice.isPaid(), "Full payment should mark the invoice as paid");
        
        double totalPaid = invoice.getPayments().stream().mapToDouble(Payment::getAmountPayed).sum();
        assertEquals(100.0, totalPaid, 0.001);
        assertEquals(invoice.getTotalDueWithFees(), totalPaid, 0.001);
    }


    
    @Test
    @DisplayName("Basic: BaseInvoiceAmount max value check (Custom Validation)")
    void testBaseInvoiceAmountMaxValue() {
        Address validAddress = new Address("St. Test", "City", "00-001", "PL");

        assertDoesNotThrow(() -> new Invoice(completedOrder, validAddress, 100000.0, null));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Invoice(completedOrder, validAddress, 100000.01, null);
        });
        assertTrue(exception.getMessage().contains("exceeds maximum allowed transaction value"));
    }

    @Test
    @DisplayName("Basic: InvoiceNumber is generated in correct format (Custom Logic)")
    void testInvoiceNumberFormat() {
        Address validAddress = new Address("St. Test", "City", "00-001", "PL");
        Invoice invoice = new Invoice(completedOrder, validAddress, 500.0, null);
        String number = invoice.getInvoiceNumber();

        assertTrue(number.matches("\\d{2}-[A-Z0-9]{7}"), "Invoice number format is incorrect: " + number);
        assertEquals(10, number.length());
    }

    
    @Test
    @DisplayName("Class: Cannot create Invoice for non-COMPLETED Order (Custom Validation)")
    void testRelatedOrderStatusCheck() {
        Address validAddress = new Address("St. Test", "City", "00-001", "PL");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            new Invoice(activeOrder, validAddress, 500.0, null);
        });
        assertTrue(exception.getMessage().contains("Cannot invoice an Order that is not yet COMPLETED"));
    }

    
    @Test
    @DisplayName("Complex: BillingAddress country not approved (Custom Validation)")
    void testBillingAddressCountryCheck() {
        Address validAddress = new Address("St. Test", "City", "00-001", "PL");
        assertDoesNotThrow(() -> new Invoice(completedOrder, validAddress, 500.0, null));

        Address invalidAddress = new Address("Rue Test", "Paris", "75001", "FR");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Invoice(completedOrder, invalidAddress, 500.0, null);
        });
        assertTrue(exception.getMessage().contains("is not an approved billing region"));
    }

    
    @Test
    @DisplayName("Multi-Value: Reject payment that causes over-payment (Custom Validation)")
    void testOverPaymentCheck() {
        Address validAddress = new Address("St. Test", "City", "00-001", "PL");
        Invoice invoice = new Invoice(completedOrder, validAddress, 100.0, null);

        Payment payment1 = new Cash(50.0, completedOrder, 50.0);
        invoice.addPayment(payment1);

        Payment payment2 = new Cash(50.0, completedOrder, 50.0);
        assertDoesNotThrow(() -> invoice.addPayment(payment2));

        Payment overPayment = new Cash(0.01, completedOrder, 0.01);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            invoice.addPayment(overPayment);
        });
        assertTrue(exception.getMessage().contains("cannot exceed Total Due"));
    }

    
    @Test
    @DisplayName("Derived: TotalDueWithFees calculation (with fee)")
    void testTotalDueWithFeesCalculationWithFee() {
        Address validAddress = new Address("St. Test", "City", "00-001", "PL");
        Invoice invoice = new Invoice(completedOrder, validAddress, 200.0, 2.5);

        double expectedTotal = 200.0 * (1.0 + 2.5 / 100.0);
        assertEquals(205.00, invoice.getTotalDueWithFees(), 0.001);
    }

    @Test
    @DisplayName("Derived: TotalDueWithFees calculation (no fee)")
    void testTotalDueWithFeesCalculationNoFee() {
        Address validAddress = new Address("St. Test", "City", "00-001", "PL");
        Invoice invoice = new Invoice(completedOrder, validAddress, 200.0, null);

        assertEquals(200.0, invoice.getTotalDueWithFees(), 0.001);
    }

    
    @Test
    @DisplayName("Optional: LateFeeRate boundary check (Custom Validation)")
    void testLateFeeRateBoundary() {
        Address validAddress = new Address("St. Test", "City", "00-001", "PL");

        assertDoesNotThrow(() -> new Invoice(completedOrder, validAddress, 10.0, null));

        Invoice invoiceMax = new Invoice(completedOrder, validAddress, 10.0, 5.0);
        assertEquals(5.0, invoiceMax.getLateFeeRate());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Invoice(completedOrder, validAddress, 10.0, 5.1);
        });
        assertTrue(exception.getMessage().contains("Late fee rate must be between 0.0% and 5.0%"));
    }

    
    @Test
    @DisplayName("Persistency: Save and load Invoice extent successfully")
    void testInvoiceExtentPersistence() throws Exception {
        Address address1 = new Address("Street 1", "City 1", "01-100", "PL");
        Address address2 = new Address("Street 2", "City 2", "02-200", "DE");

        Invoice i1 = new Invoice(completedOrder, address1, 150.0, 1.0);
        Customer customer3 = new Customer();
        Order completedOrder2 = Order.createDineIn(customer3);
        completedOrder2.finalizeOrder();
        completedOrder2.completeOrder();
        Invoice i2 = new Invoice(completedOrder2, address2, 800.0, null);

        i1.addPayment(new Cash(75.0, completedOrder, 75.0));

        assertEquals(2, Invoice.getAllInvoices().size());

        Invoice.saveExtent("test_invoices.dat");

        Invoice.clearExtent();
        assertEquals(0, Invoice.getAllInvoices().size());

        assertTrue(Invoice.loadExtent("test_invoices.dat"));

        List<Invoice> loadedInvoices = Invoice.getAllInvoices();
        assertEquals(2, loadedInvoices.size());

        Invoice loadedI1 = loadedInvoices.stream()
                .filter(i -> i.getBaseInvoiceAmount() == 150.0)
                .findFirst().orElseThrow();

        assertEquals(150.0, loadedI1.getBaseInvoiceAmount());
        assertEquals(1, loadedI1.getPayments().size());
        assertEquals(false, loadedI1.isPaid());
        assertEquals("PL", loadedI1.getBillingAddress().getCountry());

        PersistenceConfig.deleteDataFile("test_invoices.dat");
    }
}
