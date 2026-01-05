package domain.entity;

import domain.enums.CancelStatus;
import domain.enums.OrderStatus;
import domain.validation.IdValidator;

import java.time.LocalDateTime;
import java.util.*;

//注文の「今の状態」と「関連ID（支払い・キャンセル）」を持つ。
public class Order {
    //注文番号
    private final String orderId;
    //注文の状態
    private OrderStatus status;
    //キャンセル手続きの状態
    private CancelStatus cancelStatus;
    //この注文の支払い中の一件のID
    private String activePaymentId;
    //この注文の支払いのID一覧
    private List<String> paymentHistoryIds;
    //手続き中のキャンセル申請ID
    private String activeCancelId;
    //この注文で作られたキャンセル申請IDの一覧
    private List<String> cancelHistoryIds;
    //注文を作った時刻
    private final LocalDateTime createdAt;
    //注文の状態が変わる時刻
    private LocalDateTime updatedAt;

    public Order(LocalDateTime createdAt) {
        this.orderId = UUID.randomUUID().toString();
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.status = OrderStatus.CREATED;
        this.cancelStatus = CancelStatus.NONE;
        this.activePaymentId = null;
        this.paymentHistoryIds = new ArrayList<>();
        this.activeCancelId = null;
        this.cancelHistoryIds = new ArrayList<>();
        this.updatedAt = this.createdAt;
    }

    //----getter----
    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public CancelStatus getCancelStatus() {
        return cancelStatus;
    }

    public String getActivePaymentId() {
        return activePaymentId;
    }

    public List<String> getPaymentHistoryIds() {
        return Collections.unmodifiableList(paymentHistoryIds);
    }

    public String getActiveCancelId() {
        return activeCancelId;
    }

    public List<String> getCancelHistoryIds() {
        return Collections.unmodifiableList(cancelHistoryIds);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    //----Serviceに使うメソッド----

    public void changeStatus(OrderStatus newStatus, LocalDateTime now) {
        this.status = Objects.requireNonNull(newStatus, "newStatus cannot be null");
        touch(now);
    }

    public void changeCancelStatus(CancelStatus newCancelStatus, LocalDateTime now) {
        this.cancelStatus = Objects.requireNonNull(newCancelStatus, "newCancelStatus cannot be null");
        touch(now);
    }

    public void setActivePaymentId(String paymentId, LocalDateTime now) {
        IdValidator.requireUuid(paymentId, "paymentId");
        this.activePaymentId = Objects.requireNonNull(paymentId, "paymentId cannot be null");
        touch(now);
    }

    public void clearActivePaymentId(LocalDateTime now) {
        this.activePaymentId = null;
        touch(now);
    }

    public void addPaymentHistory(String paymentId, LocalDateTime now) {
        IdValidator.requireUuid(paymentId, "paymentId");
        this.paymentHistoryIds.add(Objects.requireNonNull(paymentId, "paymentId cannot be null"));
        touch(now);
    }

    public void setActiveCancelId(String cancelRequestId, LocalDateTime now) {
        IdValidator.requireUuid(cancelRequestId, "cancelRequestId");
        this.activeCancelId = Objects.requireNonNull(cancelRequestId, "cancelId cannot be null");
        touch(now);
    }

    public void clearActiveCancelId(LocalDateTime now) {
        this.activeCancelId = null;
        touch(now);
    }

    public void addCancelHistory(String cancelRequestId, LocalDateTime now) {
        IdValidator.requireUuid(cancelRequestId, "cancelRequestId");
        this.cancelHistoryIds.add(Objects.requireNonNull(cancelRequestId, "cancelRequestId cannot be null"));
        touch(now);
    }

    private void touch(LocalDateTime now) {
        this.updatedAt = Objects.requireNonNull(now, "now cannot be null");
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "status=" + status +
                ", orderId='" + orderId + '\'' +
                ", cancelStatus=" + cancelStatus +
                ", activePaymentId=" + activePaymentId +
                ", activeCancelId=" + activeCancelId +
                '}';
    }
}
