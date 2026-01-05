# 06 手動テストシナリオ（最小12本）

目的：主要ルール（R1..R15）と状態機械を手動で検証できるようにする。  
期待：失敗時は必ず REJECT と RuleNo を確認できる。

---

## A. 成功シナリオ（4本）

### S1：主ルート（完了まで）
手順：
1. createOrder
2. createPayment
3. attemptPaySuccess
4. startFulfillment
5. shipOrder
6. completeOrder
   期待：
- OrderStatus：CREATED→CONFIRMED→FULFILLING→SHIPPED→COMPLETED

### S2：支払い失敗→リトライ→成功
手順：
1. createOrder
2. createPayment
3. attemptPayFail
4. createPayment（再作成）
5. attemptPaySuccess
   期待：
- 失敗した Payment は FAILED の履歴へ
- 新しい Payment が UNPAID として活動になる
- 最後に OrderStatus は CONFIRMED

### S3：CONFIRMED でキャンセル承認（返金あり）
手順：
1. createOrder → createPayment → attemptPaySuccess（Order=CONFIRMED）
2. requestCancel
3. approveCancel
   期待：
- OrderStatus：CANCELED
- CancelRequest：APPROVED
- Payment：PAID→REFUNDED（1回）

### S4：FULFILLING 中にキャンセル拒否（快照復元）
手順：
1. createOrder → createPayment → attemptPaySuccess
2. startFulfillment（Order=FULFILLING）
3. requestCancel（requestedAtOrderStatus=FULFILLING を記録）
4. rejectCancel
   期待：
- OrderStatus：FULFILLING に復元
- CancelRequest：REJECTED
- CancelStatus：NONE、activeCancelId クリア

---

## B. 失敗シナリオ（8本：RuleNo を必ず確認）

### F5：CREATED 以外で createPayment（R1）
手順例：
1. createOrder → createPayment → attemptPaySuccess（Order=CONFIRMED）
2. createPayment（再度）
   期待：REJECT、RuleNo=R1

### F6：活動 UNPAID がある状態で createPayment（R1）
手順：
1. createOrder → createPayment（UNPAID が活動）
2. createPayment（再度）
   期待：REJECT、RuleNo=R1

### F7：activePaymentId ではない paymentId で attemptPaySuccess（R5）
手順：
1. createOrder → createPayment（PaymentA=UNPAID）
2. attemptPayFail（PaymentA=FAILED、活動クリア）
3. createPayment（PaymentB=UNPAID が活動）
4. attemptPaySuccess(paymentId=PaymentA)
   期待：REJECT、RuleNo=R5

### F8：支払い FAILED なのに注文が CONFIRMED になることを防ぐ（R4/R3）
手順：
1. createOrder → createPayment
2. attemptPayFail
   期待：
- OrderStatus は CREATED のまま（CONFIRMED にならない）
- PaymentStatus は FAILED
  （実装で矛盾が出るなら R4/R3 のチェック不備）

### F9：SHIPPED/COMPLETED で requestCancel（R6）
手順例：
- 完了ルートで Order を SHIPPED または COMPLETED にしてから requestCancel
  期待：REJECT、RuleNo=R6

### F10：CancelStatus≠REQUESTED で approve/reject（R8）
手順：
1. createOrder（CancelStatus=NONE）
2. approveCancel（または rejectCancel）
   期待：REJECT、RuleNo=R8

### F11：キャンセル承認後に履行を進める（R9）
手順：
1. createOrder → createPayment → attemptPaySuccess
2. requestCancel → approveCancel（Order=CANCELED）
3. startFulfillment / shipOrder / completeOrder のいずれかを実行
   期待：REJECT、RuleNo=R9

### F12：REFUNDED を再度 REFUNDED（重複返金禁止：R11）
手順：
1. S3 のルートで Payment を REFUNDED にする
2. approveCancel 相当の返金処理を再度発動させる（同一 Payment を対象に）
   期待：REJECT、RuleNo=R11
