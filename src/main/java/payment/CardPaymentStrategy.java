package payment;

import model.Payment;
import model.enums.PaymentStatus;

public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public void pay(Payment payment) {
        payment.setStatus(PaymentStatus.PAID);
        System.out.println("Paiement par carte effectué avec succès.");
    }
}