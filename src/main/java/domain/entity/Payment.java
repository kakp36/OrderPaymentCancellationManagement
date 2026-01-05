package domain.entity;

import domain.enums.PaymentStatus;
import domain.validation.IdValidator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

//支払い1回分の記録（状態と注文IDを持つ）。
public class Payment {
    //支払い番号
    private final String paymentId;
    //どの注文が支払いを申請した番号
    private final String orderId;
    //支払いの状態
    private PaymentStatus status;
    //支払いを作った時刻
    private final LocalDateTime createdAt;
    //支払いの状態が変更した時刻
    private LocalDateTime updatedAt;

    public Payment(String orderId, LocalDateTime createdAt) {
        this.paymentId = UUID.randomUUID().toString();
        this.orderId = IdValidator.requireUuid(orderId, "orderId");
        this.status = PaymentStatus.UNPAID;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = this.createdAt;
    }

    //----getter----
    public String getPaymentId() {
        return paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    //----Serviceに使うメソッド----
    public void changeStatus(PaymentStatus newStatus, LocalDateTime now) {
        this.status = Objects.requireNonNull(newStatus, "newStatus cannot be null");
        this.updatedAt = Objects.requireNonNull(now, "now cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(paymentId);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", orderId=" + orderId +
                ", status=" + status +
                '}';
    }
}
