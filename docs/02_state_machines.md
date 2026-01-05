# 02 状態機械（State Machines）

本書は Order / Payment / Cancel の「許可される状態遷移」を固定する。  
表にない遷移はすべて禁止とする。

---

## 1. OrderStatus（注文状態）の遷移

### 1.1 許可遷移（状態が変わる命令のみ）
- （なし）→ CREATED：Buyer.createOrder
- CREATED → CONFIRMED：Buyer.attemptPaySuccess（活動 UNPAID が成功）
- CONFIRMED → FULFILLING：Seller.startFulfillment（活動キャンセルなし）
- FULFILLING → SHIPPED：Seller.shipOrder
- SHIPPED → COMPLETED：Seller.completeOrder
- CREATED / CONFIRMED / FULFILLING → CANCELED：Seller.approveCancel または CS.forceApproveCancel
- rejectCancel：注文状態を requestedAtOrderStatus に戻す（CREATED/CONFIRMED/FULFILLING のいずれか）

### 1.2 終端（terminal）
- CANCELED / COMPLETED：外向き遷移なし（状態変更命令を禁止）

---

## 2. PaymentStatus（支払い状態）の遷移

### 2.1 許可遷移（状態が変わる命令のみ）
- （なし）→ UNPAID：Buyer.createPayment（activePaymentId を設定）
- UNPAID → PAID：Buyer.attemptPaySuccess（対象は activePaymentId のみ／成功後 activePaymentId クリア）
- UNPAID → FAILED：Buyer.attemptPayFail（対象は activePaymentId のみ／失敗後 activePaymentId クリア）
- PAID → REFUNDED：Seller.approveCancel または CS.forceApproveCancel（注文キャンセルに連動／同一 Payment は1回だけ）

---

## 3. CancelStatus（注文側フロー）の遷移

### 3.1 許可遷移（フローが変わる命令のみ）
- NONE → REQUESTED：Buyer.requestCancel（許可状態かつ活動キャンセルなし）
- REQUESTED → NONE：Seller.approveCancel（CancelRequest=APPROVED／注文→CANCELED／activeCancelId クリア）
- REQUESTED → NONE：Seller.rejectCancel（CancelRequest=REJECTED／注文は快照へ復元／activeCancelId クリア）
- REQUESTED → NONE：CS.forceApproveCancel（条件を満たす場合のみ／activeCancelId クリア）
