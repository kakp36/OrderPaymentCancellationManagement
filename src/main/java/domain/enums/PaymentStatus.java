package domain.enums;
//支払いの状態の一覧
public enum PaymentStatus {
    //未払い
    UNPAID,
    //支払い成功
    PAID,
    //支払い失敗
    FAILED,
    //返金済み
    REFUNDED
}
