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

    
    private OrderStatus status;
    private String items;
    private double total;
    private LocalDate date;
    private LocalTime time;

    
    private Customer customer;
    private List<OrderRequest> orderRequests;
    private Payment payment; 
    // Discount is not used for now for simplicity

    
    protected Order() {
        this.orderRequests = new ArrayList<>();
    }

    
    protected Order(Customer customer) {
        this.orderRequests = new ArrayList<>();
        setCustomer(customer);
        this.status = OrderStatus.ACTIVE;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.items = "";
        this.total = 0.0;
        addOrder(this);
    }

    
    public OrderStatus getStatus() { return status; }
    public String getItems() { return items; }
    public double getTotal() { return total; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public Customer getCustomer() { return customer; }
    public Payment getPayment() { return payment; }

    public List<OrderRequest> getOrderRequests() {
        return Collections.unmodifiableList(orderRequests);
    }

    
    public void setCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        this.customer = customer;
    }

    public void setPayment(Payment payment) {
        
        this.payment = payment;
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


    public void addOrderRequest(OrderRequest orderRequest) {
        if (orderRequest == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }
        if (this.status != OrderStatus.ACTIVE) {
            throw new IllegalStateException("Cannot add order request to non-active order");
        }
        orderRequests.add(orderRequest);
        recalculateTotal();
        updateItemsDescription();
    }

    // Recalculate total
    private void recalculateTotal() {
        this.total = 0.0;
        for (OrderRequest request : orderRequests) {
            this.total += request.calculateRequestTotal();
        }
    }


    private void updateItemsDescription() {
        StringBuilder itemsBuilder = new StringBuilder();
        for (OrderRequest request : orderRequests) {
            if (!itemsBuilder.isEmpty()) {
                itemsBuilder.append("; ");
            }
            itemsBuilder.append(request.getRequestDetails());
        }
        this.items = itemsBuilder.toString();
    }

    // Finalize order
    public void finalizeOrder() {
        if (orderRequests.isEmpty()) {
            throw new IllegalStateException("Cannot finalize order with no order requests");
        }
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
        if (this.payment == null || this.payment.getStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("Order cannot be completed without confirmed payment");
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
        return String.format("Order[customer=%s, status=%s, date=%s, time=%s, total=%.2f, requests=%d]",
            customer.getName(), status, date, time, total, orderRequests.size());
    }
}
