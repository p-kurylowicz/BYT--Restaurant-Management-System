import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Invoice implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private static List<Invoice> allInvoices = new ArrayList<>();
    

    // Basic Attribute
    private final String invoiceNumber;
    // Class Attribute
    private Order relatedOrder;
    // Complex Attribute
    private Address billingAddress;
    // Multi-Value Attribute
    private final List<Payment> payments = new ArrayList<>();
    // Optional Attribute
    private Double lateFeeRate;
    // Basic Attribute
    private double baseInvoiceAmount;

    // Basic Attribute
    private final LocalDate issueDate;
    // Basic Attribute
    private boolean isPaid;

    
    public Invoice(Order relatedOrder, Address billingAddress, double baseInvoiceAmount, Double lateFeeRate) {
        setRelatedOrder(relatedOrder);
        setBillingAddress(billingAddress);
        setBaseInvoiceAmount(baseInvoiceAmount);
        setLateFeeRate(lateFeeRate);

        this.invoiceNumber = generateNewInvoiceNumber();
        this.issueDate = LocalDate.now();
        this.isPaid = false;
        addInvoice(this);
    }

    
    public String getInvoiceNumber() { return invoiceNumber; }
    public Order getRelatedOrder() { return relatedOrder; }
    public Address getBillingAddress() { return billingAddress; }
    public double getBaseInvoiceAmount() { return baseInvoiceAmount; }
    public Double getLateFeeRate() { return lateFeeRate; }
    public boolean isPaid() { return isPaid; }

    public List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }

    // Derived Attribute
    public double getTotalDueWithFees() {
        double feeRate = (lateFeeRate != null) ? lateFeeRate : 0.0;
        
        return baseInvoiceAmount * (1.0 + (feeRate / 100.0));
    }


    

    // Basic Attribute
    private String generateNewInvoiceNumber() {
        String yearPrefix = String.format("%02d", LocalDate.now().getYear() % 100);
        
        return yearPrefix + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase();
    }


    // Class Attribute
    public void setRelatedOrder(Order relatedOrder) {
        if (relatedOrder == null) {
            throw new IllegalArgumentException("Invoice must be linked to a related Order");
        }
        
        if (relatedOrder.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot invoice an Order that is not yet COMPLETED.");
        }
        this.relatedOrder = relatedOrder;
    }

    // Complex Attribute
    public void setBillingAddress(Address billingAddress) {
        if (billingAddress == null) {
            throw new IllegalArgumentException("Billing address cannot be null");
        }
        this.billingAddress = billingAddress;
    }

    // Basic Attribute
    public void setBaseInvoiceAmount(double baseInvoiceAmount) {
        if (baseInvoiceAmount < 0.0) {
            throw new IllegalArgumentException("Base invoice amount cannot be negative");
        }
        
        if (baseInvoiceAmount > 100000.0) {
            throw new IllegalArgumentException("Base invoice amount exceeds maximum allowed transaction value (100000.0)");
        }
        this.baseInvoiceAmount = baseInvoiceAmount;
    }
    
    // Optional Attribute
    public void setLateFeeRate(Double lateFeeRate) {
        if (lateFeeRate != null) {
            
            if (lateFeeRate < 0.0 || lateFeeRate > 5.0) {
                throw new IllegalArgumentException("Late fee rate must be between 0.0% and 5.0%");
            }
        }
        this.lateFeeRate = lateFeeRate;
    }


    

    // Multi-Value Attribute
    public void addPayment(Payment newPayment) {
        if (newPayment == null || newPayment.getAmountPayed() <= 0.0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        
        double totalPaid = payments.stream()
                .mapToDouble(Payment::getAmountPayed)
                .sum();

        
        if (totalPaid + newPayment.getAmountPayed() > getTotalDueWithFees() + 0.001) {
            throw new IllegalStateException(
                String.format("Total payments (%.2f + %.2f) cannot exceed Total Due (%.2f)",
                totalPaid, newPayment.getAmountPayed(), getTotalDueWithFees()));
        }

        this.payments.add(newPayment);
        checkAndMarkPaid();
    }

    
    public void checkAndMarkPaid() {
        double totalPaid = payments.stream()
                .mapToDouble(Payment::getAmountPayed)
                .sum();
        
        this.isPaid = totalPaid >= getTotalDueWithFees() - 0.001;
    }


    
    private static void addInvoice(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        allInvoices.add(invoice);
    }

    public static List<Invoice> getAllInvoices() {
        return Collections.unmodifiableList(allInvoices);
    }

    public static void clearExtent() {
        allInvoices.clear();
    }

    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allInvoices);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream(filepath))) {
            allInvoices = (List<Invoice>) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allInvoices.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Invoice[number=%s, paid=%s, baseAmount=%.2f, totalDue=%.2f, payments=%d, fee=%.1f%%]",
            invoiceNumber, isPaid ? "Yes" : "No", baseInvoiceAmount, getTotalDueWithFees(), payments.size(), lateFeeRate != null ? lateFeeRate : 0.0);
    }
}
