package payment;

import model.Payment;

public interface PaymentStrategy {

    void pay(Payment payment);
}