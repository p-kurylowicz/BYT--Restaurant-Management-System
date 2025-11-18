import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Order implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    
    private static List<Order> allOrders = new ArrayList<>();
    private static int orderCounter = 0;


    private String orderId;
    private OrderStatus status;
    private String items;
    private double total;
    private LocalDate date;
    private LocalTime time;


    protected Order() {}


    protected Order(String orderId) {
        this.orderId = orderId != null ? orderId : "ORD-" + String.format("%06d", ++orderCounter);
        this.status = OrderStatus.ACTIVE;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.items = "";
        this.total = 0.0;
        addOrder(this);
    }


    public String getOrderId() { return orderId; }
    public OrderStatus getStatus() { return status; }
    public String getItems() { return items; }
    public double getTotal() { return total; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }

    /**
     * Calculates the total price of the order.
     * TODO: Implement full business logic for order total calculation
     */
    public double calculateTotal() {
        return calculateFinalPrice();
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


    // Placeholder - no order requests for now
    public void setItems(String items) {
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null");
        }
        this.items = items;
    }

    public void setTotal(double total) {
        if (total < 0) {
            throw new IllegalArgumentException("Total cannot be negative");
        }
        this.total = total;
    }

    // Finalize order
    public void finalizeOrder() {
        if (this.status != OrderStatus.ACTIVE) {
            throw new IllegalStateException("Only active orders can be finalized");
        }
        this.status = OrderStatus.AWAITING_PAYMENT;
    }


    public double calculateFinalPrice() {
        return this.total;
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
        return String.format("Order[id=%s, status=%s, date=%s, time=%s, items=%s, total=%.2f]",
            orderId, status, date, time, items, total);
    }
}
