package repository;

import domain.entity.Order;

import java.util.Optional;

//Order を保存・取得する窓口
public interface OrderRepository {
    //Orderを保存する
    void save(Order order);

    //IDでOrderを探す
    Optional<Order> findById(String orderId);

    //IDでOrderを取得する
    Order getById(String orderId);

    //Orderが存在するかを判断する
    boolean existsById(String orderId);
}
