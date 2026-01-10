package repository.memory;

import domain.entity.CancelRequest;
import domain.validation.IdValidator;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;
import repository.CancelRequestRepository;

import java.util.*;

//Map で CancelRequest を保存する実装。
public class InMemoryCancelRequestRepository implements CancelRequestRepository {
    //map　キー：cancelRequestId　value：cancelRequest
    private final Map<String, CancelRequest> cancelRequestMap = new HashMap<>();

    @Override
    public void save(CancelRequest cancelRequest) {
        if(cancelRequest == null) {
            throw new InvalidParameterException("cancelRequest must not be null");
        }
        IdValidator.requireUuid(cancelRequest.getCancelRequestId(),"cancelRequestId");
        IdValidator.requireUuid(cancelRequest.getOrderId(),"orderId");
        cancelRequestMap.put(cancelRequest.getCancelRequestId(), cancelRequest);
    }

    //IDで cancelRequest を探す
    @Override
    public Optional<CancelRequest> findById(String cancelRequestId) {
        return Optional.ofNullable(cancelRequestMap.get(cancelRequestId));
    }

    //IDで cancelRequest を取得する
    @Override
    public CancelRequest getById(String cancelRequestId) {
        CancelRequest cancelRequest = cancelRequestMap.get(cancelRequestId);
        if(cancelRequest == null) {
            throw new EntityNotFoundException("cancelRequest not found","CancelRequest",cancelRequestId);
        }
        return cancelRequest;
    }

    //該当する Order のすべての CancelRequest を一覧する
    @Override
    public List<CancelRequest> findByOrderId(String orderId) {
        List<CancelRequest> cancelRequestList = new ArrayList<>();
        for(CancelRequest cancelRequest : cancelRequestMap.values()) {
            if(cancelRequest.getOrderId().equals(orderId)) {
                cancelRequestList.add(cancelRequest);
            }
        }
        return cancelRequestList;
    }
}
