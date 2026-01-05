# 01 用語集（ドメイン辞書 v1）

本書は、本プロジェクトで使用する用語・概念の定義を固定する。

---

## 1. エンティティ（Entity）

### Order（注文）
1回の取引履行（フルフィルメント）を表す集約。注文状態と、支払い/キャンセルの関連情報を保持する。  
同一性（equals/hashCode）は orderId のみで判定する。

### Payment（支払い）
支払いの1回の試行記録。1つの注文に複数の履歴 Payment を持てるが、任意時点で活動中（UNPAID）は最大1つ。  
同一性（equals/hashCode）は paymentId のみで判定する。

### CancelRequest（キャンセル申請）
キャンセル申請の1回分の記録。1つの注文に複数の履歴 CancelRequest を持てるが、任意時点で活動中（REQUESTED）は最大1つ。  
同一性（equals/hashCode）は cancelRequestId のみで判定する。

---

## 2. ID と同一性（Identity）

- orderId：不変の一意ID。Order の同一性は orderId のみ。
- paymentId：不変の一意ID。Payment の同一性は paymentId のみ。
- cancelRequestId：不変の一意ID。CancelRequest の同一性は cancelRequestId のみ。

---

## 3. 活動（Active）と履歴（History）

### activePaymentId
Order が保持する「活動中支払い」のID。null 可。  
null でない場合、指す Payment は必ず PaymentStatus=UNPAID である。  
その Payment が PAID/FAILED になったら activePaymentId は必ずクリアする。

### activePayment（概念）
order.activePaymentId が指し、かつ UNPAID の Payment。  
attemptPaySuccess/attemptPayFail の対象として唯一許可される支払い。

### paymentHistory（概念）
FAILED/PAID/REFUNDED など「終了状態」の Payment の集合/順序。  
履歴 Payment は attemptPay* を禁止する。

### activeCancelId
Order が保持する「活動中キャンセル申請」のID。null 可。  
null でない場合、CancelStatus=REQUESTED（進行中）のときのみ。  
approve/reject 等で終了したら activeCancelId は必ずクリアする。

### activeCancel（概念）
activeCancelId が指し、かつ REQUESTED の CancelRequest。  
approveCancel/rejectCancel/forceApproveCancel の対象として唯一許可される申請。

### requestedAtOrderStatus
キャンセル申請時点の OrderStatus スナップショット。  
rejectCancel のとき、注文状態をこの値に戻すために使用する。

---

## 4. 状態（Status）と終端（Terminal）

### OrderStatus
CREATED / CONFIRMED / CANCELED / FULFILLING / SHIPPED / COMPLETED

- 終端（terminal state）：CANCELED, COMPLETED  
  終端到達後は、注文状態を変化させる命令を禁止する。

### PaymentStatus
UNPAID / PAID / FAILED / REFUNDED（戻りは禁止）

### CancelStatus（注文側フロー）
NONE / REQUESTED（進行中）  
※フロー終了後は必ず NONE に戻す。

---

## 5. ルール番号と例外（概念）

- ruleNo：R1..R15。ルール不満足は必ず一意の ruleNo に紐づける。
- BusinessRuleViolation：ルール不満足を表す業務例外（引数不正/存在しない とは区別）。

---

## 6. プロジェクト約定（固定・変更禁止）

1) キャンセル拒否後：CancelRequest は REJECTED の履歴として残す。注文側 CancelStatus は NONE に戻し、activeCancelId はクリアする。
2) 支払いリトライ：活動支払いが FAILED になった後は createPayment で新しい UNPAID を作成可能。FAILED の履歴 Payment に attemptPay* を禁止する。
