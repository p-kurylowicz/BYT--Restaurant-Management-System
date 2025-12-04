import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public abstract class Order implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    private static List<Order> allOrders = new ArrayList<>();


    private OrderStatus status;
    private LocalDate date;
    private LocalTime time;

    // Composition: Order -> Payment (1 to 1..*)
    private Set<Payment> payments;

    // Basic Association: Order -> Customer (0..1 to 0..*)
    private Customer customer;

    protected Order() {
        this.status = OrderStatus.ACTIVE;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.payments = new HashSet<>();
        addOrder(this);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer newCustomer) {
        if (this.customer != newCustomer) {
            if (this.customer != null) {
                this.customer.removeOrder(this);
            }
            this.customer = newCustomer;
            if (newCustomer != null) {
                newCustomer.addOrder(this);
            }
        }
    }

    public void removeCustomer() {
        if (this.customer != null) {
            Customer oldCustomer = this.customer;
            this.customer = null;
            oldCustomer.removeOrder(this);
        }
    }

    public OrderStatus getStatus() { return status; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }

    /**
     * Calculates the total price of the order.
     * TODO: Implement when we have inheritance
     */
    // Derived attribute
    public double getTotalAmount() {
        // Placeholder
        return 0.0;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date;
    }

    public void setTime(LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }
        this.time = time;
    }

    // Composition: Order <-> Payment (1 to 1..*)
    public Set<Payment> getPayments() {
        return Collections.unmodifiableSet(payments);
    }

    public void addPayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null - Order must have at least one Payment (1..*)");
        }

        // Prevent duplicate assignment
        if (payments.contains(payment)) {
            return; // Already connected
        }

        payments.add(payment);

        // Establish reverse connection if not already set
        if (payment.getOrder() != this) {
            payment.setOrder(this);
        }
    }

    public void removePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }

        // Check if payment exists first
        if (!payments.contains(payment)) {
            throw new IllegalArgumentException("This payment is not part of this order");
        }

        // Enforce 1..* multiplicity: cannot remove last payment
        if (payments.size() <= 1) {
            throw new IllegalStateException("Cannot remove the last payment. Order must have at least one payment (1..*)");
        }

        payments.remove(payment);

        // Note: Cannot remove order from payment as it's composition - payment cannot exist without order
        // The payment should be deleted from extent when removed from order
    }

    // Package-private method to directly add without constraint check (for reverse connection)
    void addPaymentDirect(Payment payment) {
        payments.add(payment);
    }

    // Finalize order
    public void finalizeOrder() {
        if (this.status != OrderStatus.ACTIVE) {
            throw new IllegalStateException("Only active orders can be finalized");
        }
        this.status = OrderStatus.AWAITING_PAYMENT;
    }



    // Complete order (after payment)
    public void completeOrder() {
        if (this.status != OrderStatus.AWAITING_PAYMENT) {
            throw new IllegalStateException("Only orders awaiting payment can be completed");
        }
        this.status = OrderStatus.COMPLETED;
    }

    // Cancel order
    public void cancelOrder() {
        if (this.status == OrderStatus.AWAITING_PAYMENT || this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel order that is awaiting payment or completed");
        }
        this.status = OrderStatus.CANCELLED;
    }

    
    private static void addOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        allOrders.add(order);
    }

    public static List<Order> getAllOrders() {
        return Collections.unmodifiableList(allOrders);
    }

    public static void clearExtent() {
        allOrders.clear();
    }

    
    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allOrders);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            allOrders = (List<Order>) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allOrders.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Order[status=%s, date=%s, time=%s]",
            status, date, time);
    }
}
