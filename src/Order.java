import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Order implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static List<Order> allOrders = new ArrayList<>();

    private OrderStatus status;
    private LocalDate date;
    private LocalTime time;

    private final Set<Payment> payments;
    private Customer customer;
    private Discount discount;

    // Dynamic inheritance via composition (separate fields)
    // XOR constraint: Exactly one must be non-null
    private DineIn dineIn;      // 0..1 - can be null
    private Takeaway takeaway;  // 0..1 - can be null

    // Factory methods (complete constraint)
    public static Order createDineIn(Customer customer) {
        Order order = new Order(customer);
        order.dineIn = new DineIn(order);
        order.takeaway = null;
        return order;
    }

    public static Order createDineIn(Customer customer, Reservation reservation) {
        Order order = new Order(customer);
        order.dineIn = new DineIn(order, reservation);
        order.takeaway = null;
        return order;
    }

    public static Order createTakeaway(Customer customer) {
        Order order = new Order(customer);
        order.takeaway = new Takeaway(order);
        order.dineIn = null;
        return order;
    }

    public static Order createTakeaway(Customer customer, LocalTime collectionTime) {
        Order order = new Order(customer);
        order.takeaway = new Takeaway(order, collectionTime);
        order.dineIn = null;
        return order;
    }

    private Order(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null - Order must have a Customer");
        }

        this.status = OrderStatus.ACTIVE;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.payments = new HashSet<>();

        addOrderToExtent(this);

        this.customer = customer;
        customer.addOrder(this);
    }

    // Type checking (disjoint constraint)
    public boolean isDineIn() {
        return dineIn != null;
    }

    public boolean isTakeaway() {
        return takeaway != null;
    }

    public OrderKind getKind() {
        if (isDineIn()) return OrderKind.DINE_IN;
        if (isTakeaway()) return OrderKind.TAKEAWAY;
        throw new IllegalStateException("Order must be either DineIn or Takeaway (complete constraint)");
    }

    // Component access (type-safe)
    public DineIn getDineIn() {
        if (!isDineIn()) {
            throw new IllegalStateException("Order is not DINE_IN");
        }
        return dineIn;
    }

    public Takeaway getTakeaway() {
        if (!isTakeaway()) {
            throw new IllegalStateException("Order is not TAKEAWAY");
        }
        return takeaway;
    }

    private void ensureCanChangeKind() {
        if (this.status == OrderStatus.AWAITING_PAYMENT || this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change order kind when awaiting payment or completed");
        }
    }

    public void changeToDineIn() {
        ensureCanChangeKind();

        // Delete old component
        this.takeaway = null;

        // Create new component
        this.dineIn = new DineIn(this);
    }

    public void changeToDineIn(Reservation reservation) {
        ensureCanChangeKind();

        // Delete old component
        this.takeaway = null;

        // Create new component
        this.dineIn = new DineIn(this, reservation);
    }

    public void changeToTakeaway() {
        ensureCanChangeKind();

        // Cleanup old component
        if (isDineIn()) {
            dineIn.releaseTables();
        }
        this.dineIn = null;

        // Create new component
        this.takeaway = new Takeaway(this);
    }

    public void changeToTakeaway(LocalTime collectionTime) {
        ensureCanChangeKind();

        // Cleanup old component
        if (isDineIn()) {
            dineIn.releaseTables();
        }
        this.dineIn = null;

        // Create new component
        this.takeaway = new Takeaway(this, collectionTime);
    }

    public void markAsPickedUp() {
        if (!isTakeaway()) {
            throw new IllegalStateException("Only takeaway orders can be marked as picked up");
        }
        if (getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Order must be completed before marking as picked up");
        }
        getTakeaway().markAsPickedUp();
    }

    public Customer getCustomer() { return customer; }

    public void setCustomer(Customer newCustomer) {
        if (this.customer == newCustomer) return;

        if (this.customer != null) {
            Customer oldCustomer = this.customer;
            this.customer = null;
            oldCustomer.removeOrder(this);
        }

        this.customer = newCustomer;

        if (newCustomer != null && !newCustomer.getOrders().contains(this)) {
            newCustomer.addOrder(this);
        }
    }

    public void removeCustomer() {
        if (this.customer != null) {
            Customer oldCustomer = this.customer;
            this.customer = null;
            oldCustomer.removeOrder(this);
        }
    }

    public void delete() {
        if (isDineIn()) {
            dineIn.releaseTables();
        }

        List<Payment> paymentsCopy = new ArrayList<>(payments);
        for (Payment payment : paymentsCopy) {
            payment.delete();
        }
        payments.clear();

        if (this.customer != null) {
            Customer oldCustomer = this.customer;
            this.customer = null;
            oldCustomer.removeOrder(this);
        }

        allOrders.remove(this);
    }

    public Set<Payment> getPayments() {
        return Collections.unmodifiableSet(payments);
    }

    public void addPayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null - Order must have at least one Payment (1..*)");
        }
        if (payments.contains(payment)) return;

        payments.add(payment);

        if (payment.getOrder() != this) {
            payment.setOrder(this);
        }
    }

    public void removePayment(Payment payment) {
        if (payment == null) throw new IllegalArgumentException("Payment cannot be null");

        if (!payments.contains(payment)) {
            throw new IllegalArgumentException("This payment is not part of this order");
        }

        if (payments.size() <= 1) {
            throw new IllegalStateException("Cannot remove the last payment. Order must have at least one payment (1..*)");
        }

        payments.remove(payment);
        payment.delete();
    }

    void addPaymentDirect(Payment payment) {
        payments.add(payment);
    }

    public OrderStatus getStatus() { return status; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }

    public Discount getDiscount() { return discount; }
    public void setDiscount(Discount discount) { this.discount = discount; }
    public void removeDiscount() { this.discount = null; }
    public boolean hasDiscount() { return discount != null; }

    public double getSubtotal() { return 0.0; }

    public double getTotalAmount() {
        double subtotal = getSubtotal();
        if (discount != null && discount.validateDiscount(this)) {
            if (discount.isOrderLevel()) {
                double percentage = discount.getDiscountPercentage();
                return subtotal * (1 - percentage / 100.0);
            }
        }
        return subtotal;
    }

    public void setDate(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Date cannot be null");
        this.date = date;
    }

    public void setTime(LocalTime time) {
        if (time == null) throw new IllegalArgumentException("Time cannot be null");
        this.time = time;
    }

    public void finalizeOrder() {
        if (this.status != OrderStatus.ACTIVE) {
            throw new IllegalStateException("Only active orders can be finalized");
        }
        this.status = OrderStatus.AWAITING_PAYMENT;
    }

    public void completeOrder() {
        if (this.status != OrderStatus.AWAITING_PAYMENT) {
            throw new IllegalStateException("Only orders awaiting payment can be completed");
        }
        this.status = OrderStatus.COMPLETED;

        if (isDineIn()) {
            dineIn.releaseTables();
        }
    }

    public void cancelOrder() {
        if (this.status == OrderStatus.AWAITING_PAYMENT || this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel order that is awaiting payment or completed");
        }
        this.status = OrderStatus.CANCELLED;

        if (isDineIn()) {
            dineIn.releaseTables();
        }
    }

    private static void addOrderToExtent(Order order) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null");
        allOrders.add(order);
    }

    public static List<Order> getAllOrdersFromExtent() {
        return Collections.unmodifiableList(allOrders);
    }

    /**
     * View order history for a specific customer.
     * Returns all orders sorted by date (most recent first).
     */
    public static List<Order> viewOrderHistory(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        return allOrders.stream()
                .filter(o -> o.getCustomer().equals(customer))
                .sorted((o1, o2) -> {
                    int dateCompare = o2.getDate().compareTo(o1.getDate());
                    if (dateCompare != 0) return dateCompare;
                    return o2.getTime().compareTo(o1.getTime());
                })
                .toList();
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
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Order[kind=%s, status=%s, date=%s, time=%s",
                getKind(), status, date, time));

        if (isDineIn()) {
            sb.append(", ").append(dineIn.toString());
        } else if (isTakeaway()) {
            sb.append(", ").append(takeaway.toString());
        }

        sb.append("]");
        return sb.toString();
    }
}
