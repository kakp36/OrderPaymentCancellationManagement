# 03 フィールド一覧と不変条件（Invariants）

本書は、状態機械・ルールを支えるための「保持すべきフィールド」と「常に成り立つべき条件」を固定する。

---

## 1. Order（注文）

### 1.1 フィールド
- orderId（不変）
- status（可変：OrderStatus）
- cancelStatus（可変：CancelStatus）
- activePaymentId（null 可）
- paymentHistoryIds（順序リスト）
- activeCancelId（null 可）
- cancelHistoryIds（順序リスト）
- createdAt（不変）
- updatedAt（可変）

### 1.2 不変条件（O-INV）
- O-INV-1：orderId は null 不可、かつ不変。
- O-INV-2：status∈{CANCELED, COMPLETED} のとき、status を変える命令を禁止する。
- O-INV-3：activePaymentId が非 null の場合、指す Payment は存在し status=UNPAID。
- O-INV-4：同一注文で UNPAID は最大1つ。UNPAID が存在するなら activePaymentId はそれを指す。
- O-INV-5：cancelStatus=REQUESTED ⇔ activeCancelId 非 null。cancelStatus=NONE ⇒ activeCancelId は null。
- O-INV-6：cancelStatus=REQUESTED の間は startFulfillment を禁止する。
- O-INV-7：paymentHistoryIds の各 paymentId は存在し、かつ orderId が一致する。
- O-INV-8：cancelHistoryIds の各 cancelRequestId は存在し、かつ orderId が一致する。
- O-INV-9：注文が CANCELED かつ PAID が存在する場合、必ず PAID→REFUNDED を実行する（一次性）。
- O-INV-10：状態/関連フィールド変更後は updatedAt を更新する。

---

## 2. Payment（支払い）

### 2.1 フィールド
- paymentId（不変）
- orderId（不変）
- status（可変：PaymentStatus）
- createdAt（不変）
- updatedAt（可変）

### 2.2 不変条件（P-INV）
- P-INV-1：paymentId は null 不可、かつ不変。
- P-INV-2：orderId は null 不可、かつ不変。
- P-INV-3：status の遷移は UNPAID→PAID/FAILED、PAID→REFUNDED のみ。戻り・飛び越しは禁止。
- P-INV-4：UNPAID の Payment は、その注文の activePaymentId が指すもののみ許可（それ以外は違反）。

---

## 3. CancelRequest（キャンセル申請）

### 3.1 フィールド
- cancelRequestId（不変）
- orderId（不変）
- status（REQUESTED/APPROVED/REJECTED）
- requestedAtOrderStatus（不変：スナップショット）
- createdAt（不変）
- updatedAt（可変）

### 3.2 不変条件（C-INV）
- C-INV-1：cancelRequestId と orderId は null 不可、かつ不変。
- C-INV-2：status の遷移は REQUESTED→APPROVED または REQUESTED→REJECTED のみ。
- C-INV-3：rejectCancel では requestedAtOrderStatus により注文状態を復元する（推測復元は禁止）。
