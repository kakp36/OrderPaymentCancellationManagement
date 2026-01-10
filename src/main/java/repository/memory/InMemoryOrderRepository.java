package repository.memory;

import domain.entity.Order;
import domain.validation.IdValidator;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;
import repository.OrderRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//Map で Order を保存する実装。
public class InMemoryOrderRepository implements OrderRepository {
    //map　キー：OrderId　value：Order
    private final Map<String, Order> orderMap = new HashMap<>();

    @Override
    public void save(Order order) {
        if(order == null) {
            throw new InvalidParameterException("order must not be null");
        }
        IdValidator.requireUuid(order.getOrderId(),"orderId");
        orderMap.put(order.getOrderId(), order);
    }

    //IDで Order を探す
    @Override
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orderMap.get(orderId));
    }

    //IDで Order を取得する
    @Override
    public Order getById(String orderId) {
        Order order = orderMap.get(orderId);
        if(order == null) {
            throw new EntityNotFoundException("order not found","Order",orderId);
        }
        return order;
    }

    //IDで該当する Order が存在するかを判断する
    @Override
    public boolean existsById(String orderId) {
        return orderMap.containsKey(orderId);
    }
}
