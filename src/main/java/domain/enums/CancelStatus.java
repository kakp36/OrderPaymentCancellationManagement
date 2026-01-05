package domain.enums;
/**
 * 注文の「進行中キャンセルは常に1件だけ」という不変条件を守るため、注文側は REQUESTED のみを進行中として扱い、完了(承認/却下)時
 * は必ず NONE に戻して activeCancelId をクリアする（APPROVED/REJECTED は申請履歴の最終結果）。
 */
public enum CancelStatus {
    //キャンセル手続きなし
    NONE,
    //キャンセル申請中
    REQUESTED,
    //キャンセル承認
    APPROVED,
    //キャンセル拒否
    REJECTED
}
