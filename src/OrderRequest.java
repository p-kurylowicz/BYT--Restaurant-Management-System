import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OrderRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    
    private static List<OrderRequest> allOrderRequests = new ArrayList<>();

    
    private String requestId;
    private OrderRequestStatus status;
    private String requestDetails;

    
    private List<ItemQuantity> itemQuantities;

    
    public OrderRequest() {
        this.itemQuantities = new ArrayList<>();
        this.requestId = UUID.randomUUID().toString();
        this.status = OrderRequestStatus.PENDING;
        this.requestDetails = "";
        addOrderRequest(this);
    }

    
    public String getRequestId() { return requestId; }
    public OrderRequestStatus getStatus() { return status; }
    public String getRequestDetails() { return requestDetails; }

    public List<ItemQuantity> getItemQuantities() {
        return Collections.unmodifiableList(itemQuantities);
    }

    
    public void setRequestId(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            throw new IllegalArgumentException("Request ID cannot be null or empty");
        }
        this.requestId = requestId.trim();
    }

    public void setRequestDetails(String requestDetails) {
        if (requestDetails == null) {
            this.requestDetails = "";
        } else {
            this.requestDetails = requestDetails.trim();
        }
    }


    public void addItemQuantity(ItemQuantity itemQuantity) {
        if (itemQuantity == null) {
            throw new IllegalArgumentException("Item quantity cannot be null");
        }
        itemQuantities.add(itemQuantity);
        updateRequestDetails();
    }


    private void updateRequestDetails() {
        StringBuilder details = new StringBuilder();
        for (ItemQuantity iq : itemQuantities) {
            if (!details.isEmpty()) {
                details.append(", ");
            }
            details.append(iq.getMenuItem().getName()).append(" x").append(iq.getQuantity());
        }
        this.requestDetails = details.toString();
    }


    public double calculateRequestTotal() {
        double total = 0.0;
        for (ItemQuantity iq : itemQuantities) {
            total += iq.getItemTotal();
        }
        return total;
    }

    // Confirm request
    public void confirmRequest() {
        if (itemQuantities.isEmpty()) {
            throw new IllegalStateException("Cannot confirm request with no items");
        }
        this.status = OrderRequestStatus.CONFIRMED;
    }

    // Mark as served
    public void markAsServed() {
        this.status = OrderRequestStatus.SERVED;
        for (ItemQuantity iq : itemQuantities) {
            iq.markAsServed();
        }
    }

    // Change status
    public void changeStatus(OrderRequestStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = newStatus;
    }

    // Cancel order request
    public void cancelOrderRequest() {
        if (this.status == OrderRequestStatus.IN_PREPARATION) {
            throw new IllegalStateException("Cannot cancel order request that is already in preparation");
        }
        // Additional cancellation logic would go here
    }

    
    private static void addOrderRequest(OrderRequest orderRequest) {
        if (orderRequest == null) {
            throw new IllegalArgumentException("OrderRequest cannot be null");
        }
        allOrderRequests.add(orderRequest);
    }

    public static List<OrderRequest> getAllOrderRequests() {
        return Collections.unmodifiableList(allOrderRequests);
    }

    public static void clearExtent() {
        allOrderRequests.clear();
    }

    
    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allOrderRequests);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            allOrderRequests = (List<OrderRequest>) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allOrderRequests.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("OrderRequest[id=%s, status=%s, items=%d, total=%.2f, details=%s]",
            requestId, status, itemQuantities.size(), calculateRequestTotal(), requestDetails);
    }
}
