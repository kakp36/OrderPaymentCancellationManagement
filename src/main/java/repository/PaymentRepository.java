package repository;

import domain.entity.Payment;

import java.util.List;
import java.util.Optional;

//Payment を保存・取得する窓口
public interface PaymentRepository {
    //paymentを保存する
    void save(Payment payment);

    //IDでpaymentを探す
    Optional<Payment> findById(String paymentId);

    //IDでpaymentを取得する
    Payment getById(String paymentId);

    //該当するOrderのすべてのpaymentを一覧する
    List<Payment> findByOrderId(String orderId);

}
