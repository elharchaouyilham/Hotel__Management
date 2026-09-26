package service;

import model.Payment;
import repository.PaymentRepository;
import payment.PaymentStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment makePayment(
            UUID userId,
            UUID reservationId,
            BigDecimal amount,
            PaymentStrategy strategy) {

        if (userId == null) {
            throw new IllegalArgumentException("Utilisateur invalide.");
        }

        if (reservationId == null) {
            throw new IllegalArgumentException("Réservation invalide.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
        }

        Payment payment = new Payment(
                UUID.randomUUID(),
                userId,
                reservationId,
                amount,
                null,
                LocalDateTime.now()
        );

        strategy.pay(payment);

        paymentRepository.save(payment);

        return payment;
    }
}