CREATE TABLE IF NOT EXISTS reservations (
                                            id UUID PRIMARY KEY,
                                            code VARCHAR(50) UNIQUE NOT NULL,

    user_id UUID NOT NULL,
    room_id UUID NOT NULL,

    check_in DATE NOT NULL,
    check_out DATE NOT NULL,

    total_price DECIMAL(10,2) NOT NULL,

    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_reservation_user
    FOREIGN KEY (user_id)
    REFERENCES users(id),

    CONSTRAINT fk_reservation_room
    FOREIGN KEY (room_id)
    REFERENCES rooms(id),

    CONSTRAINT check_dates
    CHECK (check_out > check_in)
    );