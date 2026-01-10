package repository;

import domain.entity.CancelRequest;

import java.util.List;
import java.util.Optional;

//CancelRequest を保存・取得する窓口
public interface CancelRequestRepository {
    //キャンセル申請を保存する
    void save(CancelRequest cancelRequest);

    //IDでキャンセル申請を探す
    Optional<CancelRequest> findById(String cancelRequestId);

    //IDでキャンセル申請を取得する
    CancelRequest getById(String cancelRequestId);

    //該当するOrderのすべてのキャンセル申請を一覧する
    List<CancelRequest> findByOrderId(String orderId);
}
