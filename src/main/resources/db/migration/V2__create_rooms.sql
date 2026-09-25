CREATE TABLE IF NOT EXISTS rooms (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    number INTEGER UNIQUE NOT NULL,
    type VARCHAR(40) NOT NULL
    CHECK (type IN ('SUITE', 'DOUBLE', 'SINGLE')),
    price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL
    CHECK (status IN ('AVAILABLE', 'MAINTENANCE'))
    );