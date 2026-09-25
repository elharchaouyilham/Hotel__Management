package model;

import model.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {

    private UUID id;
    private UUID userId;
    private UUID reservationId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime paymentDate;

    public Payment() {
    }

    public Payment(UUID id, UUID userId, UUID reservationId, BigDecimal amount, PaymentStatus status,
                   LocalDateTime paymentDate) {
        this.id = id;
        this.userId = userId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.status = status;
        this.paymentDate = paymentDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
}