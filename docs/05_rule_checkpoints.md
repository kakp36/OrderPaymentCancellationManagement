# 05 ルール検証ポイント対照表（Command → Pre/Post → RuleNo）

本書は、各コマンド（命令）が「どの RuleNo をどこで検証するか」を固定する。  
Pre=実行前チェック、Post=実行後に成立すべき条件。

---

## Buyer（購入者）

### createOrder
- Pre：引数の妥当性（パラメータチェックのみ）
- Post：注文初期値（status=CREATED、cancelStatus=NONE、activePaymentId/activeCancelId=null）

### createPayment
- Pre：R1（OrderStatus=CREATED かつ活動 UNPAID が無い）
- Post：R2（Payment=UNPAID、activePaymentId 設定、活動 UNPAID は1つ）

### attemptPaySuccess
- Pre：R3（活動かつ UNPAID）、R5（paymentId=activePaymentId）
- Post：R3（Payment→PAID、Order CREATED→CONFIRMED）、activePaymentId クリア

### attemptPayFail
- Pre：R4（活動かつ UNPAID）、R5（paymentId=activePaymentId）
- Post：R4（Payment→FAILED、Order は CREATED のまま）、activePaymentId クリア
- 備考：支払いリトライ（活動支払いが無い状態に戻ること）

### requestCancel
- Pre：R6（SHIPPED/COMPLETED 禁止）、R7（状態許可 + 活動キャンセル無し）
- Post：CancelStatus=REQUESTED、activeCancelId 設定、CancelRequest に requestedAtOrderStatus 記録

---

## Seller（販売者）

### approveCancel
- Pre：R8（CancelStatus=REQUESTED）
- Post：R9（Order→CANCELED、CancelRequest→APPROVED）
- Post（条件付き）：R11（PAID が存在するなら PAID→REFUNDED を1回だけ）

### rejectCancel
- Pre：R8（CancelStatus=REQUESTED）
- Post：R10（Order は requestedAtOrderStatus に復元、CancelRequest→REJECTED）
- Post：CancelStatus→NONE、activeCancelId クリア

### startFulfillment
- Pre：R12（OrderStatus=CONFIRMED かつ CancelStatus≠REQUESTED）
- Post：Order→FULFILLING

### shipOrder
- Pre：R13（OrderStatus=FULFILLING）
- Post：Order→SHIPPED

### completeOrder
- Pre：R14（OrderStatus=SHIPPED）
- Post：Order→COMPLETED

---

## Customer Support（客服）

### forceApproveCancel
- Pre：R15（CancelStatus=REQUESTED + 発動条件 + 理由必須）
- Post：approveCancel と同等（Order→CANCELED、必要なら返金、監査ログ出力）
