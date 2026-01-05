A) 非代码产出物最终版参照（Step 1~Step 6）
A1.《领域词典 v1》（最终版）

实体

Order：一次交易履约聚合体；判等仅按 orderId。

Payment：一次支付尝试记录；同订单可多笔历史，但任一时刻最多 1 笔活动支付。

CancelRequest：一次取消申请记录；同订单任一时刻最多 1 笔活动取消。

ID 与相等性

orderId / paymentId / cancelRequestId：不可变唯一标识；equals/hashCode 仅按各自 id。

活动对象与历史

activePaymentId：可空；非空当且仅当指向 PaymentStatus=UNPAID；当该 Payment 变为 PAID/FAILED 时必须清空。

activePayment：order.activePaymentId 指向且 UNPAID 的 Payment；唯一允许 attemptPay* 的对象。

paymentHistory：所有已结束状态（FAILED/PAID/REFUNDED）的 Payment；历史禁止 attemptPay*。

activeCancelId：可空；非空当且仅当 CancelStatus=REQUESTED；审批结束必须清空。

activeCancel：activeCancelId 指向且 REQUESTED 的 CancelRequest；唯一允许审批的对象。

requestedAtOrderStatus：取消发起时订单状态快照；rejectCancel 用它恢复。

状态（终态）

OrderStatus：CREATED / CONFIRMED / CANCELED / FULFILLING / SHIPPED / COMPLETED

终态：CANCELED、COMPLETED（终态后禁止任何改变订单状态的命令）

PaymentStatus：UNPAID / PAID / FAILED / REFUNDED（不可回退）

CancelStatus（订单侧流程）：NONE / REQUESTED（进行中）/（流程结束后必须回 NONE）

规则与异常

ruleNo：R1..R15，任何“规则不满足”必须定位到唯一 ruleNo。

BusinessRuleViolation：规则不满足异常（区别参数非法与找不到实体）。

项目约定（固定，不改口）

cancel 被拒绝：CancelRequest 保留 REJECTED 历史；订单侧 CancelStatus 回 NONE，activeCancelId 清空。

支付重试：活动支付 FAILED 后允许再次 createPayment 创建新 UNPAID；旧 FAILED 禁止 attemptPay*。

A2.《状态机总览表》（最终版）
A2.1 OrderStatus 允许迁移

（无）→ CREATED：Buyer.createOrder

CREATED → CONFIRMED：Buyer.attemptPaySuccess（活动 UNPAID 支付成功）

CONFIRMED → FULFILLING：Seller.startFulfillment（无活动取消）

FULFILLING → SHIPPED：Seller.shipOrder

SHIPPED → COMPLETED：Seller.completeOrder

CREATED/CONFIRMED/FULFILLING → CANCELED：Seller.approveCancel 或 CS.forceApproveCancel（取消同意）

rejectCancel：订单恢复为 requestedAtOrderStatus（可能是 CREATED/CONFIRMED/FULFILLING 之一）

终态约束

CANCELED / COMPLETED：无任何向外迁移

A2.2 PaymentStatus 允许迁移

（无）→ UNPAID：Buyer.createPayment（写入 activePaymentId）

UNPAID → PAID：Buyer.attemptPaySuccess（必须是 activePaymentId；成功后清空 activePaymentId）

UNPAID → FAILED：Buyer.attemptPayFail（必须是 activePaymentId；失败后清空 activePaymentId）

PAID → REFUNDED：Seller.approveCancel 或 CS.forceApproveCancel（订单取消触发；同一 Payment 只一次）

A2.3 CancelStatus（订单侧流程）允许迁移

NONE → REQUESTED：Buyer.requestCancel（订单状态允许区间内，且无活动取消）

REQUESTED → NONE：Seller.approveCancel（CancelRequest=APPROVED；订单→CANCELED；清空 activeCancelId）

REQUESTED → NONE：Seller.rejectCancel（CancelRequest=REJECTED；订单恢复快照；清空 activeCancelId）

REQUESTED → NONE：CS.forceApproveCancel（满足超时/异常触发；清空 activeCancelId）

A3.《字段清单与不变量》（最终版）
A3.1 Order 字段

orderId（不可变）

status（可变）

cancelStatus（可变）

activePaymentId（可空）

paymentHistoryIds（顺序列表）

activeCancelId（可空）

cancelHistoryIds（顺序列表）

createdAt（不可变）

updatedAt（可变）

A3.2 Order 不变量（可判定）

O-INV-1：orderId 非空且不可变

O-INV-2：status∈{CANCELED,COMPLETED} 禁止任何命令改变 status

O-INV-3：activePaymentId 非空 ⇒ 指向 Payment 且其 status=UNPAID

O-INV-4：任一订单任一时刻最多 1 个 UNPAID；若存在 UNPAID，则 activePaymentId 必须指向它

O-INV-5：cancelStatus=REQUESTED ⇔ activeCancelId 非空；cancelStatus=NONE ⇒ activeCancelId 必为空

O-INV-6：cancelStatus=REQUESTED 时禁止 startFulfillment

O-INV-7：paymentHistoryIds 中每个 paymentId 必须存在且归属本 orderId

O-INV-8：cancelHistoryIds 中每个 cancelId 必须存在且归属本 orderId

O-INV-9：订单 CANCELED 且存在 PAID ⇒ 必须触发对应 PAID→REFUNDED（一次性）

O-INV-10：任何状态/关联字段变化后 updatedAt 必更新

A3.3 Payment 字段与不变量

字段

paymentId（不可变）

orderId（不可变）

status（可变）

createdAt（不可变）

updatedAt（可变）

不变量

P-INV-1：paymentId 非空且不可变

P-INV-2：orderId 非空且不可变

P-INV-3：status 只允许 UNPAID→PAID/FAILED，PAID→REFUNDED；禁止回退与跳跃

P-INV-4：UNPAID 的 Payment 必须是其订单的 activePaymentId 指向对象（否则违规）

A3.4 CancelRequest 字段与不变量

字段

cancelRequestId（不可变）

orderId（不可变）

status（REQUESTED/APPROVED/REJECTED）

requestedAtOrderStatus（不可变）

createdAt（不可变）

updatedAt（可变）

不变量

C-INV-1：cancelRequestId/orderId 非空且不可变

C-INV-2：status 只允许 REQUESTED→APPROVED 或 REQUESTED→REJECTED

C-INV-3：rejectCancel 时必须使用 requestedAtOrderStatus 恢复订单状态（不得拍脑袋恢复）

A4.《规则目录 + 校验点对照表》（Step 4 最终版）

规则写法标准：每条规则必须能落到某个命令的“前置/后置校验”。

Buyer.createOrder

前置：参数合法（仅参数校验）

后置：Order 初始 status=CREATED，cancelStatus=NONE，activePaymentId/activeCancelId 为空（词典约定）

Buyer.createPayment

前置：R1（OrderStatus=CREATED 且无活动 UNPAID）

后置：R2（Payment=UNPAID 且成为唯一活动支付 activePaymentId）

Buyer.attemptPaySuccess

前置：R3（活动且 UNPAID）、R5（paymentId 必须等于 activePaymentId）

后置：R3（Payment→PAID，Order CREATED→CONFIRMED）、清空 activePaymentId（词典约定）

Buyer.attemptPayFail

前置：R4（活动且 UNPAID）、R5（paymentId=activePaymentId）

后置：R4（Payment→FAILED，Order 保持 CREATED）、清空 activePaymentId（词典约定）、R7（允许重试的前提：无活动支付）

Buyer.requestCancel

前置：R6（SHIPPED/COMPLETED 禁止）、R7（仅 CREATED/CONFIRMED/FULFILLING 且无活动取消）

后置：CancelStatus=REQUESTED；写 activeCancelId；CancelRequest 记录 requestedAtOrderStatus（词典约定）

Seller.approveCancel

前置：R8（CancelStatus=REQUESTED）

后置：R9（Order→CANCELED，CancelRequest→APPROVED），R11（若存在 PAID 则该 Payment→REFUNDED 一次性）

Seller.rejectCancel

前置：R8（CancelStatus=REQUESTED）

后置：R10（Order 恢复 requestedAtOrderStatus，CancelRequest→REJECTED），订单 CancelStatus→NONE，清空 activeCancelId（词典约定）

Seller.startFulfillment

前置：R12（OrderStatus=CONFIRMED 且无活动取消）

后置：Order→FULFILLING（状态机）

Seller.shipOrder

前置：R13（OrderStatus=FULFILLING）

后置：Order→SHIPPED（状态机）

Seller.completeOrder

前置：R14（OrderStatus=SHIPPED）

后置：Order→COMPLETED（状态机）

CS.forceApproveCancel

前置：R15（CancelStatus=REQUESTED 且满足触发条件 + 强制理由必填）

后置：等同 approveCancel + 输出“强制理由”（日志策略）

A5.《内存存储设计》（Step 5 最终版）

ordersById：Map<orderId, Order>（订单唯一真源）

paymentsById：Map<paymentId, Payment>

cancelById：Map<cancelRequestId, CancelRequest>

一致性/去重原则

Map key 即去重；equals/hashCode 仅按 id，避免字段变化导致集合错乱

Order 内只存关联 id（paymentHistoryIds/cancelHistoryIds/activePaymentId/activeCancelId），不直接嵌对象（避免双写不一致）

任何跨表归属必须校验（Payment.orderId 与 Order.orderId 一致）

A6.《异常与日志策略》（Step 6 最终版）
异常分类

参数非法：InvalidParameter（null/空/格式不对）

找不到实体：EntityNotFound（仓库无该 id）

规则不满足：BusinessRuleViolation（必须带 ruleNo + role + command + orderId）

日志/输出（控制台）

命令审计行：time | role | command | targetIds | RESULT(SUCCESS/REJECT) | ruleNo(可空) | message

状态变化行：time | entityType | entityId | field(status/cancelStatus/paymentStatus/activeId) | from -> to | byCommand

A7.《手工验收用例清单》（最小 12 条，覆盖关键规则）

成功用例

创建订单→创建支付→支付成功→备货→发货→完成（主链路）

创建订单→创建支付→支付失败→重试创建支付→支付成功

CONFIRMED 后 requestCancel→approveCancel→订单 CANCELED + 退款

FULFILLING 中 requestCancel→rejectCancel→订单恢复 FULFILLING（用快照）

失败用例（必须命中指定 ruleNo）
5) 非 CREATED 下 createPayment（命中 R1）
6) 有活动 UNPAID 时再次 createPayment（命中 R1）
7) 对非 activePaymentId 的 paymentId attemptPaySuccess（命中 R5）
8) 支付 FAILED 后订单变 CONFIRMED（应被 R4/R3 逻辑拦截）
9) SHIPPED/COMPLETED 下 requestCancel（命中 R6）
10) CancelStatus≠REQUESTED 时 approveCancel/rejectCancel（命中 R8）
11) cancel APPROVED 后仍能 startFulfillment/ship/complete（命中终态约束 O-INV-2 或相应前置规则）
12) 对已 REFUNDED 的 Payment 再次退款（命中“一次性退款”约束 R11）A) 非代码产出物最终版参照（Step 1~Step 6）
A1.《领域词典 v1》（最终版）

实体

Order：一次交易履约聚合体；判等仅按 orderId。

Payment：一次支付尝试记录；同订单可多笔历史，但任一时刻最多 1 笔活动支付。

CancelRequest：一次取消申请记录；同订单任一时刻最多 1 笔活动取消。

ID 与相等性

orderId / paymentId / cancelRequestId：不可变唯一标识；equals/hashCode 仅按各自 id。

活动对象与历史

activePaymentId：可空；非空当且仅当指向 PaymentStatus=UNPAID；当该 Payment 变为 PAID/FAILED 时必须清空。

activePayment：order.activePaymentId 指向且 UNPAID 的 Payment；唯一允许 attemptPay* 的对象。

paymentHistory：所有已结束状态（FAILED/PAID/REFUNDED）的 Payment；历史禁止 attemptPay*。

activeCancelId：可空；非空当且仅当 CancelStatus=REQUESTED；审批结束必须清空。

activeCancel：activeCancelId 指向且 REQUESTED 的 CancelRequest；唯一允许审批的对象。

requestedAtOrderStatus：取消发起时订单状态快照；rejectCancel 用它恢复。

状态（终态）

OrderStatus：CREATED / CONFIRMED / CANCELED / FULFILLING / SHIPPED / COMPLETED

终态：CANCELED、COMPLETED（终态后禁止任何改变订单状态的命令）

PaymentStatus：UNPAID / PAID / FAILED / REFUNDED（不可回退）

CancelStatus（订单侧流程）：NONE / REQUESTED（进行中）/（流程结束后必须回 NONE）

规则与异常

ruleNo：R1..R15，任何“规则不满足”必须定位到唯一 ruleNo。

BusinessRuleViolation：规则不满足异常（区别参数非法与找不到实体）。

项目约定（固定，不改口）

cancel 被拒绝：CancelRequest 保留 REJECTED 历史；订单侧 CancelStatus 回 NONE，activeCancelId 清空。

支付重试：活动支付 FAILED 后允许再次 createPayment 创建新 UNPAID；旧 FAILED 禁止 attemptPay*。

A2.《状态机总览表》（最终版）
A2.1 OrderStatus 允许迁移

（无）→ CREATED：Buyer.createOrder

CREATED → CONFIRMED：Buyer.attemptPaySuccess（活动 UNPAID 支付成功）

CONFIRMED → FULFILLING：Seller.startFulfillment（无活动取消）

FULFILLING → SHIPPED：Seller.shipOrder

SHIPPED → COMPLETED：Seller.completeOrder

CREATED/CONFIRMED/FULFILLING → CANCELED：Seller.approveCancel 或 CS.forceApproveCancel（取消同意）

rejectCancel：订单恢复为 requestedAtOrderStatus（可能是 CREATED/CONFIRMED/FULFILLING 之一）

终态约束

CANCELED / COMPLETED：无任何向外迁移

A2.2 PaymentStatus 允许迁移

（无）→ UNPAID：Buyer.createPayment（写入 activePaymentId）

UNPAID → PAID：Buyer.attemptPaySuccess（必须是 activePaymentId；成功后清空 activePaymentId）

UNPAID → FAILED：Buyer.attemptPayFail（必须是 activePaymentId；失败后清空 activePaymentId）

PAID → REFUNDED：Seller.approveCancel 或 CS.forceApproveCancel（订单取消触发；同一 Payment 只一次）

A2.3 CancelStatus（订单侧流程）允许迁移

NONE → REQUESTED：Buyer.requestCancel（订单状态允许区间内，且无活动取消）

REQUESTED → NONE：Seller.approveCancel（CancelRequest=APPROVED；订单→CANCELED；清空 activeCancelId）

REQUESTED → NONE：Seller.rejectCancel（CancelRequest=REJECTED；订单恢复快照；清空 activeCancelId）

REQUESTED → NONE：CS.forceApproveCancel（满足超时/异常触发；清空 activeCancelId）

A3.《字段清单与不变量》（最终版）
A3.1 Order 字段

orderId（不可变）

status（可变）

cancelStatus（可变）

activePaymentId（可空）

paymentHistoryIds（顺序列表）

activeCancelId（可空）

cancelHistoryIds（顺序列表）

createdAt（不可变）

updatedAt（可变）

A3.2 Order 不变量（可判定）

O-INV-1：orderId 非空且不可变

O-INV-2：status∈{CANCELED,COMPLETED} 禁止任何命令改变 status

O-INV-3：activePaymentId 非空 ⇒ 指向 Payment 且其 status=UNPAID

O-INV-4：任一订单任一时刻最多 1 个 UNPAID；若存在 UNPAID，则 activePaymentId 必须指向它

O-INV-5：cancelStatus=REQUESTED ⇔ activeCancelId 非空；cancelStatus=NONE ⇒ activeCancelId 必为空

O-INV-6：cancelStatus=REQUESTED 时禁止 startFulfillment

O-INV-7：paymentHistoryIds 中每个 paymentId 必须存在且归属本 orderId

O-INV-8：cancelHistoryIds 中每个 cancelId 必须存在且归属本 orderId

O-INV-9：订单 CANCELED 且存在 PAID ⇒ 必须触发对应 PAID→REFUNDED（一次性）

O-INV-10：任何状态/关联字段变化后 updatedAt 必更新

A3.3 Payment 字段与不变量

字段

paymentId（不可变）

orderId（不可变）

status（可变）

createdAt（不可变）

updatedAt（可变）

不变量

P-INV-1：paymentId 非空且不可变

P-INV-2：orderId 非空且不可变

P-INV-3：status 只允许 UNPAID→PAID/FAILED，PAID→REFUNDED；禁止回退与跳跃

P-INV-4：UNPAID 的 Payment 必须是其订单的 activePaymentId 指向对象（否则违规）

A3.4 CancelRequest 字段与不变量

字段

cancelRequestId（不可变）

orderId（不可变）

status（REQUESTED/APPROVED/REJECTED）

requestedAtOrderStatus（不可变）

createdAt（不可变）

updatedAt（可变）

不变量

C-INV-1：cancelRequestId/orderId 非空且不可变

C-INV-2：status 只允许 REQUESTED→APPROVED 或 REQUESTED→REJECTED

C-INV-3：rejectCancel 时必须使用 requestedAtOrderStatus 恢复订单状态（不得拍脑袋恢复）

A4.《规则目录 + 校验点对照表》（Step 4 最终版）

规则写法标准：每条规则必须能落到某个命令的“前置/后置校验”。

Buyer.createOrder

前置：参数合法（仅参数校验）

后置：Order 初始 status=CREATED，cancelStatus=NONE，activePaymentId/activeCancelId 为空（词典约定）

Buyer.createPayment

前置：R1（OrderStatus=CREATED 且无活动 UNPAID）

后置：R2（Payment=UNPAID 且成为唯一活动支付 activePaymentId）

Buyer.attemptPaySuccess

前置：R3（活动且 UNPAID）、R5（paymentId 必须等于 activePaymentId）

后置：R3（Payment→PAID，Order CREATED→CONFIRMED）、清空 activePaymentId（词典约定）

Buyer.attemptPayFail

前置：R4（活动且 UNPAID）、R5（paymentId=activePaymentId）

后置：R4（Payment→FAILED，Order 保持 CREATED）、清空 activePaymentId（词典约定）、R7（允许重试的前提：无活动支付）

Buyer.requestCancel

前置：R6（SHIPPED/COMPLETED 禁止）、R7（仅 CREATED/CONFIRMED/FULFILLING 且无活动取消）

后置：CancelStatus=REQUESTED；写 activeCancelId；CancelRequest 记录 requestedAtOrderStatus（词典约定）

Seller.approveCancel

前置：R8（CancelStatus=REQUESTED）

后置：R9（Order→CANCELED，CancelRequest→APPROVED），R11（若存在 PAID 则该 Payment→REFUNDED 一次性）

Seller.rejectCancel

前置：R8（CancelStatus=REQUESTED）

后置：R10（Order 恢复 requestedAtOrderStatus，CancelRequest→REJECTED），订单 CancelStatus→NONE，清空 activeCancelId（词典约定）

Seller.startFulfillment

前置：R12（OrderStatus=CONFIRMED 且无活动取消）

后置：Order→FULFILLING（状态机）

Seller.shipOrder

前置：R13（OrderStatus=FULFILLING）

后置：Order→SHIPPED（状态机）

Seller.completeOrder

前置：R14（OrderStatus=SHIPPED）

后置：Order→COMPLETED（状态机）

CS.forceApproveCancel

前置：R15（CancelStatus=REQUESTED 且满足触发条件 + 强制理由必填）

后置：等同 approveCancel + 输出“强制理由”（日志策略）

A5.《内存存储设计》（Step 5 最终版）

ordersById：Map<orderId, Order>（订单唯一真源）

paymentsById：Map<paymentId, Payment>

cancelById：Map<cancelRequestId, CancelRequest>

一致性/去重原则

Map key 即去重；equals/hashCode 仅按 id，避免字段变化导致集合错乱

Order 内只存关联 id（paymentHistoryIds/cancelHistoryIds/activePaymentId/activeCancelId），不直接嵌对象（避免双写不一致）

任何跨表归属必须校验（Payment.orderId 与 Order.orderId 一致）

A6.《异常与日志策略》（Step 6 最终版）
异常分类

参数非法：InvalidParameter（null/空/格式不对）

找不到实体：EntityNotFound（仓库无该 id）

规则不满足：BusinessRuleViolation（必须带 ruleNo + role + command + orderId）

日志/输出（控制台）

命令审计行：time | role | command | targetIds | RESULT(SUCCESS/REJECT) | ruleNo(可空) | message

状态变化行：time | entityType | entityId | field(status/cancelStatus/paymentStatus/activeId) | from -> to | byCommand

A7.《手工验收用例清单》（最小 12 条，覆盖关键规则）

成功用例

创建订单→创建支付→支付成功→备货→发货→完成（主链路）

创建订单→创建支付→支付失败→重试创建支付→支付成功

CONFIRMED 后 requestCancel→approveCancel→订单 CANCELED + 退款

FULFILLING 中 requestCancel→rejectCancel→订单恢复 FULFILLING（用快照）

失败用例（必须命中指定 ruleNo）
5) 非 CREATED 下 createPayment（命中 R1）
6) 有活动 UNPAID 时再次 createPayment（命中 R1）
7) 对非 activePaymentId 的 paymentId attemptPaySuccess（命中 R5）
8) 支付 FAILED 后订单变 CONFIRMED（应被 R4/R3 逻辑拦截）
9) SHIPPED/COMPLETED 下 requestCancel（命中 R6）
10) CancelStatus≠REQUESTED 时 approveCancel/rejectCancel（命中 R8）
11) cancel APPROVED 后仍能 startFulfillment/ship/complete（命中终态约束 O-INV-2 或相应前置规则）
12) 对已 REFUNDED 的 Payment 再次退款（命中“一次性退款”约束 R11）