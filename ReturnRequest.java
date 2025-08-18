package Order_return_manager;

public class ReturnRequest {
    int id;
    String orderId;
    String customerName;
    String reason;
    String status;

    public ReturnRequest(int id, String orderId, String customerName, String reason, String status) {
        this.id = id;
        this.orderId = orderId;
        this.customerName = customerName;
        this.reason = reason;
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Order ID: %s | Name: %s | Reason: %s | Status: %s",
                id, orderId, customerName, reason, status);
    }
}
