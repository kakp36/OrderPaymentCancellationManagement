# 04 ルール番号対照表（RuleNo Dictionary）

本書は、業務ルール番号（R1..R15）の意味を固定する唯一の参照先。  
例外・ログ・テストは必ず RuleNo を出力し、意味の確認は本書で行う。

---

## R1（createPayment：前提）
注文が CREATED で、かつ活動中の UNPAID 支払いが無い場合のみ createPayment を許可する。

## R2（createPayment：結果）
createPayment 後は UNPAID 支払いを作成し activePaymentId に設定する（同一注文で活動中 UNPAID は1つだけ）。

## R3（attemptPaySuccess：前提+結果）
paymentId=activePaymentId かつ UNPAID の時のみ attemptPaySuccess を許可し、支払い→PAID、注文 CREATED→CONFIRMED にする。

## R4（attemptPayFail：前提+結果）
paymentId=activePaymentId かつ UNPAID の時のみ attemptPayFail を許可し、支払い→FAILED、注文は CONFIRMED にしない。

## R5（attemptPay*：対象制約）
attemptPaySuccess/attemptPayFail は activePaymentId の支払いにのみ適用し、履歴の支払いには適用しない。

## R6（requestCancel：禁止条件）
注文が SHIPPED または COMPLETED の場合、requestCancel を禁止する。

## R7（requestCancel：前提）
注文が CREATED/CONFIRMED/FULFILLING で、かつ活動中キャンセルが無い場合のみ requestCancel を許可する（REQUESTED は1つだけ）。

## R8（approve/reject/force：前提）
キャンセルが REQUESTED の場合のみ approveCancel / rejectCancel（forceApproveCancel）を許可する。

## R9（approveCancel：结果）
approveCancel 後、キャンセル→APPROVED、注文→CANCELED にし、その後の履行（備貨/発送/完了）を禁止する。  
※注文が CANCELED の場合、startFulfillment / shipOrder / completeOrder はすべて禁止し、違反は RuleNo=R9 として扱う。

## R10（rejectCancel：結果）
rejectCancel 後、キャンセル→REJECTED、注文は requestedAtOrderStatus に戻し、キャンセル処理を終了（注文側 CancelStatus→NONE）。

## R11（返金：一回のみ）
注文が CANCELED になり PAID が存在する場合、必ず REFUNDED にする（二重返金は禁止）。

## R12（startFulfillment：前提）
注文が CONFIRMED かつキャンセル処理中ではない場合のみ startFulfillment を許可（CONFIRMED→FULFILLING）。

## R13（shipOrder：前提）
注文が FULFILLING の場合のみ shipOrder を許可（FULFILLING→SHIPPED）。

## R14（completeOrder：前提）
注文が SHIPPED の場合のみ completeOrder を許可（SHIPPED→COMPLETED）。

## R15（forceApproveCancel：前提+監査）
キャンセルが REQUESTED かつ条件（超過時間/例外フラグ等）を満たす場合のみ forceApproveCancel を許可し、理由を必ず記録して監査ログに出す。
