package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Invoice {

    private UUID id;
    private UUID reservationId;
    private String invoiceNumber;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public Invoice() {
    }

    public Invoice(UUID id,
                   UUID reservationId,
                   String invoiceNumber,
                   BigDecimal amount,
                   LocalDateTime createdAt) {

        this.id = id;
        this.reservationId = reservationId;
        this.invoiceNumber = invoiceNumber;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}