## Repository と Service の責務境界

### Repository の責務
- エンティティの保存・取得を担当する（このプロジェクトではメモリ上の Map が唯一の真実）。
- `getById` 系で対象が存在しない場合は `EntityNotFoundException` を送出する。
- `findById` 系は「存在しない可能性がある取得」とし、`Optional.empty()` を返す。

### Repository の禁止事項
- 業務ルール（R1..R15）の判定をしない。  
  例：状態チェック、キャンセル可否、支払いの一意性、返金条件などを Repository に書かない。

### Service の責務
- 業務コマンド（createPayment / requestCancel / approveCancel など）を実行する。
- `RuleChecker` を用いて業務ルール（R1..R15）を判定する。
- ルール違反時は `BusinessRuleViolationException`（必ず ruleNo を含む）で拒否する。
- `AuditLogger` により、コマンド監査ログと状態変更ログを出力する。