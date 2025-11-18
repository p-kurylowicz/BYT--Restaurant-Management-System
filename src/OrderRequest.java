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


    public OrderRequest() {
        this.requestId = UUID.randomUUID().toString();
        this.status = OrderRequestStatus.PENDING;
        this.requestDetails = "";
        addOrderRequest(this);
    }


    public String getRequestId() { return requestId; }
    public OrderRequestStatus getStatus() { return status; }
    public String getRequestDetails() { return requestDetails; }


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

    // Confirm request
    public void confirmRequest() {
        if (this.status != OrderRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be confirmed");
        }
        this.status = OrderRequestStatus.CONFIRMED;
    }

    /**
     * Start preparation of the order request.
     * TODO: Implement full kitchen workflow logic
     */
    public void startPreparation() {
        this.status = OrderRequestStatus.IN_PREPARATION;
    }

    /**
     * Mark order request as ready for pickup/serving.
     * TODO: Implement notification logic
     */
    public void markAsReady() {
        this.status = OrderRequestStatus.READY;
    }

    // Mark as served
    public void markAsServed() {
        if (this.status != OrderRequestStatus.READY) {
            throw new IllegalStateException("Only ready requests can be marked as served");
        }
        this.status = OrderRequestStatus.SERVED;
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
        return String.format("OrderRequest[id=%s, status=%s, details=%s]",
            requestId, status, requestDetails);
    }
}
