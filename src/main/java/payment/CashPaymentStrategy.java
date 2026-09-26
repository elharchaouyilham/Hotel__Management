package payment;

import model.Payment;
import model.enums.PaymentStatus;

public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public void pay(Payment payment) {
        payment.setStatus(PaymentStatus.PAID);
        System.out.println("Paiement en espèces effectué avec succès.");
    }
}