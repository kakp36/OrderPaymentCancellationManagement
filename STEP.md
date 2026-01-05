Step C1

目标：建项目与包结构（空类/空文件即可）

输入：A0 的包与文件清单

产出：所有包与类文件已创建

验收标准：IDEA 能正常编译空项目（无实现也能通过编译）

Step C2

目标：实现 4 个 enum（OrderStatus/PaymentStatus/CancelStatus/RuleNo）

输入：A2 状态机 + A4 规则编号

产出：enum 文件齐全，值完整

验收标准：无缺值；RuleNo 精确 R1..R15

Step C3

目标：实现 3 个异常类（仅定义字段与构造需求）

输入：A6 异常策略

产出：BusinessRuleViolation / EntityNotFound / InvalidParameter（类已可用）

验收标准：BusinessRuleViolation 能承载 ruleNo/role/command/orderId

Step C4

目标：实现 3 个实体的字段、构造、equals/hashCode（只按 id）

输入：A3 字段清单与相等性约束

产出：Order/Payment/CancelRequest 可被创建、可被 Map/Set 稳定去重

验收标准：equals/hashCode 不引用可变字段；对象能打印基本信息（toString 可选）

Step C5

目标：实现 3 个 Repository 接口

输入：A5 存储设计

产出：OrderRepository/PaymentRepository/CancelRequestRepository

验收标准：接口方法覆盖 save/find/get/按 orderId 查询（Payment/Cancel）

Step C6

目标：实现 InMemory 仓库（Map 真源）

输入：A5 一致性原则

产出：3 个 InMemory*Repository

验收标准：通过 getById 找不到会抛 EntityNotFound；save 不会产生重复 key

Step C7

目标：实现 TimeProvider（统一时间来源）

输入：A6 日志需要时间

产出：TimeProvider

验收标准：项目内所有“时间戳”不再散落获取

Step C8

目标：实现 AuditLogger（先最小可用：控制台输出）

输入：A6 日志格式

产出：AuditLogger

验收标准：能输出命令审计行与状态变化行两种格式

Step C9

目标：实现 RuleChecker（统一抛 BusinessRuleViolation）

输入：A4 规则对照表

产出：RuleChecker

验收标准：任意条件失败能抛出带 ruleNo 的异常

Step C10

目标：实现 BuyerService 的 createOrder

输入：A4 Buyer.createOrder 定义

产出：createOrder 可创建并保存 Order

验收标准：新订单字段满足词典初始约束（CREATED/NONE/无活动）

Step C11

目标：实现 BuyerService 的 createPayment

输入：R1/R2 + Payment 状态机

产出：createPayment 可创建 UNPAID 并绑定 activePaymentId

验收标准：违反 R1 必拒绝；成功后满足唯一活动支付

Step C12

目标：实现 BuyerService 的 attemptPaySuccess

输入：R3/R5 + Order/Payment 状态机

产出：支付成功能驱动 Payment→PAID、Order→CONFIRMED、清空 activePaymentId

验收标准：对非活动 paymentId 必拒绝（R5）

Step C13

目标：实现 BuyerService 的 attemptPayFail

输入：R4/R5 + 重试约定

产出：支付失败能 Payment→FAILED、Order 保持 CREATED、清空 activePaymentId

验收标准：失败后能再次 createPayment（符合重试约定）

Step C14

目标：实现 BuyerService 的 requestCancel

输入：R6/R7 + Cancel 状态机

产出：CancelRequest=REQUESTED，写 requestedAtOrderStatus，订单 CancelStatus=REQUESTED，写 activeCancelId

验收标准：SHIPPED/COMPLETED 下必拒绝（R6）；已有活动取消必拒绝（R7）

Step C15

目标：实现 SellerService 的 approveCancel / rejectCancel

输入：R8/R9/R10/R11 + 退款约定

产出：同意取消能订单→CANCELED + 必要退款；拒绝取消能回退快照并结束流程

验收标准：CancelStatus≠REQUESTED 必拒绝（R8）；结束后 CancelStatus 回 NONE 且 activeCancelId 清空

Step C16

目标：实现履约链路 startFulfillment/shipOrder/completeOrder + CS.forceApproveCancel

输入：R12/R13/R14/R15 + 终态约束

产出：履约链路与客服兜底命令可用

验收标准：取消进行中禁止备货（R12）；终态禁止外迁移（O-INV-2）；forceApproveCancel 必须带理由并满足触发条件（R15）

C17：手工验收脚本落地

目标：把 A7 的 12 条用例变成可重复执行的“执行顺序清单”（调用顺序+输入id+期望输出）

完结条件：能完整跑通 4 条成功用例，日志能复盘状态变化链

C18：失败用例逐条命中 ruleNo

目标：跑 8 条失败用例，必须稳定命中指定 ruleNo

完结条件：每条失败都能明确打印：REJECT + ruleNo + role + command + orderId

C19：一致性与终态回归检查

目标：专门打 3 类漏洞：终态外迁移、历史支付可被支付、重复退款

完结条件：这三类漏洞全部被拒绝（规则号稳定）

C20：项目封版清单

目标：输出最终《规则覆盖矩阵》（规则→用例→命令）+《状态机覆盖清单》

完结条件：R1..R15 全部至少被 1 个用例覆盖；三大状态机每个状态至少出现一次