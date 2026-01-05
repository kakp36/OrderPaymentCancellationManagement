package service.support;

import domain.enums.RuleNo;
import exception.BusinessRuleViolationException;
import exception.InvalidParameterException;

import java.util.UUID;

//条件がNGなら「どのルール違反か」を付けて止める。
public class RuleChecker {
    public void ensureRule(
            boolean condition,
            RuleNo ruleNo,
            String role,
            String command,
            UUID orderId,
            String message
    ) {
        if (!condition) {
            throw new BusinessRuleViolationException(ruleNo, role, command, orderId, message);
        }
    }

    public void ensureRule(
            boolean condition,
            RuleNo ruleNo,
            String role,
            String command,
            UUID orderId,
            UUID relatedId,
            String message
    ) {
        if (!condition) {
            throw new BusinessRuleViolationException(ruleNo, role, command, orderId, relatedId, message);
        }
    }

    public void requireNotNull(Object value, String parameterName) {
        if (value == null) {
            throw new InvalidParameterException("must not be null", parameterName, null);
        }
    }

    public void requireNotBlank(String value, String parameterName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidParameterException("must not be blank", parameterName, value);
        }
    }
}
