package domain.validation;

import java.util.UUID;

public class IdValidator {
    private IdValidator() {
    }

    /**
     * ID文字列がUUID形式かをチェックする。
     *
     * @param value     ID文字列
     * @param fieldName この値が「何のIDか」（例："orderId" / "paymentId" / "cancelRequestId"）を渡す。
     */
    public static String requireUuid(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(fieldName + " " + value + " is not a valid UUID");
        }
        return value;
    }
}
