package exception;

import java.util.UUID;

//IDで探して見つからない時の例外。
public class EntityNotFoundException extends RuntimeException {
    //オブジェクトのタイプ
    private final String entityType;
    //見つからなかったID
    private final String id;

    public EntityNotFoundException(String message, String entityType, String id) {
        super("EntityNotFound entityType=" + entityType + ", id=" + id + ", message=not found");
        this.entityType = entityType;
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getId() {
        return id;
    }

}
