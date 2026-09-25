CREATE TABLE IF NOT EXISTS payment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    reservation_id UUID NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL
    CHECK (status IN ('PENDING', 'PAID', 'FAILED')),
    payment_date TIMESTAMP NOT NULL,

    CONSTRAINT fk_payment_user
    FOREIGN KEY (user_id)
    REFERENCES users(id),

    CONSTRAINT fk_payment_reservation
    FOREIGN KEY (reservation_id)
    REFERENCES reservations(id)
    );