package service.support;

import domain.enums.AuditResult;
import domain.enums.RuleNo;

import java.time.LocalDateTime;

//ログは「命令実行」と「状態変化」
public class AuditLogger {
    private final TimeProvider timeProvider;
    public AuditLogger(TimeProvider timeProvider) {
        if (timeProvider == null) {
            throw new IllegalArgumentException("timeProvider must not be null");
        }
        this.timeProvider = timeProvider;
    }

    /**
     * @param role 実行主体（Buyer/Seller/CS）
     * @param command 実行したコマンド名
     * @param targetIds 対象ID
     * @param result 結果
     * @param ruleNo 拒否時のルール番号
     * @param message 補足メッセージ（成功/拒否理由）
     */
    public void logCommandResult(String role,
                                             String command,
                                             String targetIds,
                                             AuditResult result,
                                             RuleNo ruleNo,
                                             String message) {
        String time = String.valueOf(timeProvider.now());
        System.out.println(time
                + " | " + role
                + " | " + command
                + " | " + targetIds
                + " | " + result
                + " | " + (ruleNo == null ? "-" : ruleNo.name())
                + " | " + message);
    }

    /**
     * @param entityType 対象エンティティ種別
     * @param entityId 対象エンティティID
     * @param fieldName 変更フィールド名
     * @param from 変更前の値
     * @param to 変更後の値
     * @param byCommand 変更を起こしたコマンド名
     */
    public void logStateChange(String entityType,
                                      String entityId,
                                      String fieldName,
                                      String from,
                                      String to,
                                      String byCommand) {
        String time = String.valueOf(timeProvider.now());
        System.out.println(time
                + " | " + entityType
                + " | " + entityId
                + " | " + fieldName
                + " | " + from + " -> " + to
                + " | " + byCommand);
    }
}
