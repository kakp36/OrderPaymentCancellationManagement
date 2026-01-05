package domain.enums;
//注文の状態の一覧
public enum OrderStatus {
    //注文作成直後。支払い未確定の初期状態
    CREATED,
    //支払い成功により注文が成立した状態
    CONFIRMED,
    //注文キャンセル済みの終端状態
    CANCELED,
    //出荷準備中
    FULFILLING,
    //出荷済み
    SHIPPED,
    //取引完了の終端状態
    COMPLETED
}
