package exception;

import domain.enums.RuleNo;

import java.util.UUID;

//業務ルール違反を表す例外
public class BusinessRuleViolationException extends RuntimeException{
    private final RuleNo ruleNo;
    private final String role;
    //role が実行した命令
    private final String command;
    private final UUID orderId;
    //paymentId または cancelRequestId
    private final UUID relatedId;

    public BusinessRuleViolationException(RuleNo ruleNo, String role, String command, UUID orderId,String message) {
        super("BusinessRuleViolation"
                + " ruleNo=" + ruleNo
                + ", role=" + role
                + ", command=" + command
                + ", orderId=" + orderId
                + ", message=" + message);
        this.ruleNo = ruleNo;
        this.role = role;
        this.command = command;
        this.orderId = orderId;
        this.relatedId = null;
    }

    public BusinessRuleViolationException(RuleNo ruleNo, String role, String command, UUID orderId, UUID relatedId, String message) {
        super("BusinessRuleViolation"
                + " ruleNo=" + ruleNo
                + ", role=" + role
                + ", command=" + command
                + ", orderId=" + orderId
                + ", relatedId=" + relatedId
                + ", message=" + message);
        this.ruleNo = ruleNo;
        this.role = role;
        this.command = command;
        this.orderId = orderId;
        this.relatedId = relatedId;
    }

    public RuleNo getRuleNo() {
        return ruleNo;
    }

    public String getRole() {
        return role;
    }

    public String getCommand() {
        return command;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getRelatedId() {
        return relatedId;
    }
}
