package domain.entity;

import domain.enums.CancelStatus;
import domain.enums.OrderStatus;
import domain.validation.IdValidator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

//キャンセル申請1回分の記録（申請状態と“申請時の注文状態”を持つ）。
public class CancelRequest {
    //キャンセル申請番号
    private final String cancelRequestId;
    //このキャンセル申請が属した注文ID
    private final String orderId;
    //キャンセル申請状態
    private CancelStatus status;
    //申請した瞬間の注文の状態を保存したもの
    private final OrderStatus requestedAtOrderStatus;
    //キャンセル申請を作った時刻
    private final LocalDateTime createdAt;
    //キャンセル申請の状態が変更した時刻
    private LocalDateTime updatedAt;

    public CancelRequest(String orderId, OrderStatus requestedAtOrderStatus, LocalDateTime createdAt) {
        this.cancelRequestId = UUID.randomUUID().toString();
        this.orderId = IdValidator.requireUuid(orderId, "orderId");
        this.requestedAtOrderStatus = Objects.requireNonNull(requestedAtOrderStatus);
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.status = CancelStatus.REQUESTED;
        this.updatedAt = this.createdAt;
    }

    public String getCancelRequestId() {
        return cancelRequestId;
    }

    public String getOrderId() {
        return orderId;
    }

    public CancelStatus getStatus() {
        return status;
    }

    public OrderStatus getRequestedAtOrderStatus() {
        return requestedAtOrderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    //----Serviceに使うメソッド----
    public void changeStatus(CancelStatus newStatus, LocalDateTime now) {
        this.status = Objects.requireNonNull(newStatus);
        this.updatedAt = Objects.requireNonNull(now);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CancelRequest that = (CancelRequest) o;
        return Objects.equals(cancelRequestId, that.cancelRequestId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cancelRequestId);
    }

    @Override
    public String toString() {
        return "CancelRequest{" +
                "cancelRequestId=" + cancelRequestId +
                ", orderId=" + orderId +
                ", status=" + status +
                ", requestedAtOrderStatus=" + requestedAtOrderStatus +
                '}';
    }
}
