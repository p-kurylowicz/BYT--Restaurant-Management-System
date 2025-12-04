import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Payment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    
    private static List<Payment> allPayments = new ArrayList<>();


    private PaymentStatus status;
    private double amountPayed;

    // Composition
    private Order order;

    
    protected Payment() {}

    
    protected Payment(double amountPayed) {
        setAmountPayed(amountPayed);
        this.status = PaymentStatus.UNPAID;
        addPayment(this);
    }

    
    public PaymentStatus getStatus() { return status; }
    public double getAmountPayed() { return amountPayed; }

    
    public void setAmountPayed(double amountPayed) {
        if (amountPayed <= 0) {
            throw new IllegalArgumentException("Amount payed must be greater than zero");
        }
        this.amountPayed = amountPayed;
    }

    // Composition: Payment -> Order (1 to 1..*)
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null - Payment must have an Order (mandatory 1:1..* composition)");
        }

        // Prevent duplicate assignment
        if (this.order == order) {
            return; // Already connected
        }

        // Prevent changing order once set (composition - cannot remove association)
        if (this.order != null && this.order != order) {
            throw new IllegalStateException("Order is already set and cannot be changed - composition association cannot be removed once established");
        }

        // Set the order
        this.order = order;

        // Establish reverse connection if not already set
        if (!order.getPayments().contains(this)) {
            order.addPayment(this);
        }
    }

    // Confirm payment
    public void confirmPayment() {
        this.status = PaymentStatus.PAID;
    }

    // Fail payment
    public void failPayment() {
        this.status = PaymentStatus.UNPAID;
    }

    // Set payment in transaction (for card payments >500)
    public void setInTransaction() {
        this.status = PaymentStatus.IN_TRANSACTION;
    }


    public void processPayment() {
        confirmPayment();
    }


    private static void addPayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }
        allPayments.add(payment);
    }

    public static List<Payment> getAllPayments() {
        return Collections.unmodifiableList(allPayments);
    }

    public static void clearExtent() {
        allPayments.clear();
    }

    
    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allPayments);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            allPayments = (List<Payment>) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allPayments.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Payment[status=%s, amount=%.2f]", status, amountPayed);
    }
}
