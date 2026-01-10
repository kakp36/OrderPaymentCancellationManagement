package repository.memory;

import domain.entity.Payment;
import domain.validation.IdValidator;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;
import repository.PaymentRepository;

import java.util.*;

//Map で Payment を保存する実装。
public class InMemoryPaymentRepository implements PaymentRepository {
    //map　キー：paymentId　value：payment
    private final Map<String, Payment> paymentMap = new HashMap<>();

    @Override
    public void save(Payment payment) {
        if(payment == null) {
            throw new InvalidParameterException("payment must not be null");
        }
        IdValidator.requireUuid(payment.getPaymentId(),"paymentId");
        IdValidator.requireUuid(payment.getOrderId(),"orderId");
        paymentMap.put(payment.getPaymentId(), payment);
    }

    //IDで payment を探す
    @Override
    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(paymentMap.get(paymentId));
    }

    //IDで payment を取得する
    @Override
    public Payment getById(String paymentId) {
        Payment payment = paymentMap.get(paymentId);
        if(payment == null) {
            throw new EntityNotFoundException("payment not found","Payment",paymentId);
        }
        return payment;
    }

    //該当する Order のすべての Payment を一覧する
    @Override
    public List<Payment> findByOrderId(String orderId) {
        List<Payment> paymentList = new ArrayList<>();
        for(Payment payment : paymentMap.values()) {
            if(payment.getOrderId().equals(orderId)) {
                paymentList.add(payment);
            }
        }
        return paymentList;
    }
}
